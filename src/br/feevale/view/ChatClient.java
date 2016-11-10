package br.feevale.view;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

import org.json.JSONException;
import org.json.JSONObject;

import br.feevale.util.ProtocoloUtil;

public class ChatClient extends JFrame {

	String usuario, url = "localhost";
	int porta = 2222;
	Boolean isConnected = false;

	Socket sock;
	BufferedReader reader;
	PrintWriter writer;

	private JButton btnConextar;
	private JButton btnDesconctar;
	private JButton btnEnviar;
	private JScrollPane jScrollPane1;
	private JLabel lblUrl;
	private JLabel lblPorta;
	private JLabel lblUsuario;
	private JTextArea taChat;
	private JTextField txtUrl;
	private JTextField txtChat;
	private JTextField txtPorta;
	private JTextField txtUsuario;
	private JButton btnAnexar;

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ChatClient().setVisible(true);
			}
		});
	}

	public void ListenThread() {
		Thread incomingReader = new Thread(new IncomingReader());
		incomingReader.start();
	}

	public void removerUsuario(String usuario) {
		taChat.append(usuario + " esta offline.\n");
	}

	public void enviarDesconexao() {
		// String bye = (usuario + ": :Disconectou");
		// try {
		// writer.println(bye);
		// writer.flush();
		// } catch (Exception e) {
		// taChat.append("Erro ao enviar mensagem de desconex�o.\n");
		// }
	}

	public void desconectar() {
		try {
			taChat.append("Desconectado.\n");
			sock.close();
		} catch (Exception ex) {
			taChat.append("Falha ao desconectar. \n");
		}
		isConnected = false;
		txtUsuario.setEditable(true);

	}

	public ChatClient() {
		inicializarTela();
	}

	public class IncomingReader implements Runnable {

		@Override
		public void run() {
			String stream;

			try {
				while ((stream = reader.readLine()) != null) {

					JSONObject msgJson = ProtocoloUtil.converterMsgJson(stream);
					exibirMsg(msgJson);
				}
			} catch (Exception ex) {
				System.out.print("Error" + ex.getMessage());
			}
		}

		private void exibirMsg(JSONObject json) {
			try {
				if (!usuario.equals(json.get("Usuario").toString())) {
					this.verificarExistenciaArquivo(json);
					taChat.append(json.get("Usuario").toString() + ": " + json.get("Mensagem").toString() + "\n");
				}
			} catch (JSONException e) {
				System.out.println("Erro ao ler mensagem de json (Client)");
				e.printStackTrace();
			}
		}

		private void verificarExistenciaArquivo(JSONObject json) {
			try {
				if(json.get("Arquivo") != null && json.get("Arquivo").toString() != null){
					JSONObject arquivoJson = new JSONObject(json.get("Arquivo").toString());
					if(arquivoJson.get("Conteudo") != null){
						StringBuilder nomeArq = new StringBuilder(arquivoJson.get("Nome").toString());
						nomeArq.append(".").append(arquivoJson.get("Tipo").toString());
						int dialogResult = JOptionPane.showConfirmDialog (null, "Deseja efetuar do arquivo '" + nomeArq.toString() + "' ? ","Warning", 2);
						if(dialogResult == JOptionPane.YES_OPTION){
							baixarArquivo(arquivoJson, nomeArq);
						}
					}
				}
			} catch (JSONException e) {
				System.out.println("Mensagem não contém arquivo.");
			} catch (FileNotFoundException e) {
				System.out.println("Diretório não encontrado ou sem permissão.");
			} catch (IOException e) {
				System.out.println("Erro ao gravar arquivo.");
				e.printStackTrace();
			}
		}

		private void baixarArquivo(JSONObject arquivoJson, StringBuilder nomeArq)
				throws FileNotFoundException, JSONException, IOException {
			FileOutputStream fos = new FileOutputStream("/home/kone/Downloads/" + nomeArq.toString());
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			byte[] bytes = (byte[]) arquivoJson.get("Conteudo");
			int current = bytes.length;
			int bytesReabytes = 0;
			do {
				bytesReabytes = bytes.length;
				if(current >= 0){
					current += current;
				}
			} while(current > -1);
			
			bos.write(bytes, 0 , current);
			bos.flush();
		}
	}

	private void inicializarTela() {

		lblUrl = new JLabel();
		txtUrl = new JTextField();
		lblPorta = new JLabel();
		txtPorta = new JTextField();
		lblUsuario = new JLabel();
		txtUsuario = new JTextField();
		btnConextar = new JButton();
		btnDesconctar = new JButton();
		jScrollPane1 = new JScrollPane();
		taChat = new JTextArea();
		txtChat = new JTextField();
		btnEnviar = new JButton();
		btnAnexar = new JButton();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Chat Java");
		setResizable(false);

		lblUrl.setText("URL : ");
		txtUrl.setText("localhost");

		lblPorta.setText("Porta :");
		txtPorta.setText("2222");

		lblUsuario.setText("Usuario :");

		btnConextar.setText("Conectar");
		btnConextar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				conectar(evt);
			}
		});

		btnDesconctar.setText("Desconectar");
		btnDesconctar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				disconectar(evt);
			}
		});

		taChat.setColumns(20);
		taChat.setRows(5);
		jScrollPane1.setViewportView(taChat);

		btnEnviar.setText("Enviar");
		btnEnviar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					enviarMsg(evt);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		btnAnexar.setText("Anexar");
		btnAnexar.setEnabled(false);
		btnAnexar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				anexarAquivo(evt);
			}
		});

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);

		layout.setHorizontalGroup(layout
				.createParallelGroup(
						Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup().addContainerGap()
								.addGroup(layout.createParallelGroup(Alignment.LEADING)
										.addGroup(layout.createSequentialGroup()
												.addComponent(txtChat, GroupLayout.PREFERRED_SIZE, 500,
														GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(
														btnEnviar, GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE))
										.addComponent(jScrollPane1).addGroup(
												layout.createSequentialGroup()
														.addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
																.addComponent(lblUsuario, GroupLayout.DEFAULT_SIZE, 62,
																		Short.MAX_VALUE)
																.addComponent(lblUrl, GroupLayout.DEFAULT_SIZE,
																		GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
														.addGap(18, 18, 18)
														.addGroup(layout.createParallelGroup(Alignment.LEADING, false)
																.addComponent(txtUrl, GroupLayout.DEFAULT_SIZE, 89,
																		Short.MAX_VALUE)
																.addComponent(txtUsuario))
														.addGap(18, 18, 18)
														.addGroup(layout.createParallelGroup(Alignment.LEADING, false)
																.addComponent(lblPorta, GroupLayout.DEFAULT_SIZE,
																		GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
														.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
														.addGroup(layout.createParallelGroup(Alignment.LEADING, false)
																.addComponent(txtPorta, GroupLayout.DEFAULT_SIZE, 50,
																		Short.MAX_VALUE))
														.addPreferredGap(
																LayoutStyle.ComponentPlacement.RELATED)
														.addGroup(layout.createParallelGroup(Alignment.LEADING)
																.addGroup(layout.createSequentialGroup()
																		.addComponent(btnConextar).addGap(2, 2, 2)
																		.addComponent(btnDesconctar).addGap(2, 2, 2)
																		.addGap(0, 0, Short.MAX_VALUE)))
														.addPreferredGap(
																LayoutStyle.ComponentPlacement.RELATED)
														.addGroup(layout.createParallelGroup(Alignment.LEADING)
																.addGroup(layout.createSequentialGroup()
																		.addComponent(btnAnexar).addGap(2, 2, 2)
																		.addGap(0, 0, Short.MAX_VALUE)))))
								.addContainerGap())
				.addGroup(Alignment.TRAILING, layout.createSequentialGroup()
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGap(201, 201, 201)));

		layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(lblUrl)
								.addComponent(txtUrl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(lblPorta).addComponent(txtPorta, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(layout.createParallelGroup(Alignment.LEADING, false).addComponent(txtUsuario)
								.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(lblUsuario)
										.addComponent(btnConextar).addComponent(btnAnexar)
										.addComponent(btnDesconctar)))
						.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 310, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(txtChat)
								.addComponent(btnEnviar, GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)));
		pack();
	}

	private void conectar(ActionEvent evt) {
		if (isConnected == false) {
			usuario = txtUsuario.getText();
			txtUsuario.setEditable(false);
			btnAnexar.setEnabled(true);

			try {
				sock = new Socket(url, porta);
				InputStreamReader streamreader = new InputStreamReader(sock.getInputStream());
				reader = new BufferedReader(streamreader);
				writer = new PrintWriter(sock.getOutputStream());
				JSONObject msgJson = ProtocoloUtil.montaMsgJson(usuario, " conectado.");
				writer.println(msgJson.toString());
				writer.flush();
				isConnected = true;
			} catch (Exception ex) {
				taChat.append("Erro de conexao! Tente novamente. \n");
				txtUsuario.setEditable(true);
			}

			ListenThread();

		} else if (isConnected == true) {
			taChat.append("Voce ja esta conectado. \n");
		}
	}

	private void disconectar(ActionEvent evt) {
		enviarDesconexao();
		desconectar();
	}

	private void enviarMsg(ActionEvent evt) throws JSONException {

		if (this.existeMensagemParaEnviar(txtChat.getText())) {
			try {
				JSONObject msgJson = ProtocoloUtil.montaMsgJson(usuario, txtChat.getText());
				writer.println(msgJson.toString());
				writer.flush();

			} catch (Exception ex) {
				taChat.append("Erro ao enviar mensagem. \n");
			}
			txtChat.setText("");
			txtChat.requestFocus();
		}
		this.resetarCamposEnvio();
	}

	private void anexarAquivo(ActionEvent evt) {
		JFileChooser fc = new JFileChooser(new File("C:\\"));
		fc.setDialogTitle("Anexar Arquivo");
		int resultado = fc.showSaveDialog(null);
		
		if (resultado == JFileChooser.APPROVE_OPTION) {
			File fi = fc.getSelectedFile(); // Pega o caminho do arquivo.
			System.out.printf("Caminho do arquivo: " + fi);
			
			if (fi.exists() == true) {
				System.out.print("Arquivo carregado com sucesso");
			} else {
				System.out.print("Falha ao carregar arquivo.");
			}
			
			int len = (int)fi.length();
			byte[] arquivoByte = new byte[len];
			FileInputStream inputFile  = null;
		     
			try {
				FileWriter fw = new FileWriter(fi.getPath());
				fw.flush();
				fw.close(); 
				
				inputFile = new FileInputStream(fi);         
				inputFile.read(arquivoByte, 0, len);  
				
				System.out.print(arquivoByte);
				System.out.print(inputFile.read(arquivoByte, 0, len));
				
			} catch (Exception e) {
				System.out.printf(e.getMessage());
			}
		}
//		String path = "/home/kone/Imagens/8213e2b9121b11c16d18a02e2899d724.jpg";
//		
//		JSONObject msgJson = ProtocoloUtil.montaMsgJson(usuario, txtChat.getText(), path);
//		writer.println(msgJson.toString());
//		writer.flush();
		
	}

	private boolean existeMensagemParaEnviar(String msg) {
		if (msg.equals("")) {
			return false;
		}
		return true;
	}

	private void resetarCamposEnvio() {
		txtChat.setText("");
		txtChat.requestFocus();
	}

}
