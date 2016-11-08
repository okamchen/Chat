package br.feevale.view;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

import org.json.JSONException;
import org.json.JSONObject;

import br.feevale.util.ProtocoloUtil;

public class ClientChat extends JFrame {

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
	private JFileChooser btAnexar;

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ClientChat().setVisible(true);
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
//		String bye = (usuario + ": :Disconectou");
//		try {
//			writer.println(bye);
//			writer.flush();
//		} catch (Exception e) {
//			taChat.append("Erro ao enviar mensagem de desconex�o.\n");
//		}
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

	public ClientChat() {
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
				if(!usuario.equals(json.get("Usuario").toString())){
					taChat.append(json.get("Usuario").toString() + ": " + json.get("Mensagem").toString() + "\n");
				}
			} catch (JSONException e) {
				System.out.println("Erro ao ler mensagem de json (Client)");
				e.printStackTrace();
			}
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
		btAnexar = new JFileChooser();

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
		
		//btAnexar.setToolTipText("Anexar");
		btAnexar.setBounds(20, 30, 200, 30);
		btAnexar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});



		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);

		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup().addContainerGap()
								.addGroup(layout
										.createParallelGroup(Alignment.LEADING).addGroup(layout
												.createSequentialGroup().addComponent(txtChat,
														GroupLayout.PREFERRED_SIZE,
														352, GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(btnEnviar, GroupLayout.DEFAULT_SIZE, 111,
														Short.MAX_VALUE))
										.addComponent(jScrollPane1)
										.addGroup(layout.createSequentialGroup().addGroup(layout
												.createParallelGroup(Alignment.TRAILING, false)
												.addComponent(lblUsuario, GroupLayout.DEFAULT_SIZE, 62,
														Short.MAX_VALUE)
												.addComponent(lblUrl, GroupLayout.DEFAULT_SIZE,
														GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
												.addGap(18, 18, 18)
												.addGroup(layout
														.createParallelGroup(Alignment.LEADING,
																false)
														.addComponent(txtUrl, GroupLayout.DEFAULT_SIZE, 89,
																Short.MAX_VALUE)
														.addComponent(txtUsuario))
												.addGap(18, 18, 18)
												.addGroup(layout
														.createParallelGroup(Alignment.LEADING,
																false)
														.addComponent(lblPorta, GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
												.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
												.addGroup(layout.createParallelGroup(
														Alignment.LEADING, false).addComponent(
																txtPorta, GroupLayout.DEFAULT_SIZE, 50,
																Short.MAX_VALUE))
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(layout
														.createParallelGroup(Alignment.LEADING)
														.addGroup(
																layout.createSequentialGroup().addComponent(btnConextar)
																		.addGap(2, 2, 2).addComponent(btnDesconctar)
																		.addGap(0, 0, Short.MAX_VALUE)))))
								.addContainerGap())
				.addGroup(Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGap(201, 201, 201)));

		layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(lblUrl)
						.addComponent(txtUrl, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPorta).addComponent(txtPorta, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(txtUsuario)
						.addGroup(layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblUsuario).addComponent(btnConextar)
								.addComponent(btnDesconctar)))
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 310,
						GroupLayout.PREFERRED_SIZE)
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
	
		if (this.existeMensagemParaEnviar(txtChat.getText())){
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
	
	private boolean existeMensagemParaEnviar(String msg){
		if(msg.equals("")){
			this.resetarCamposEnvio();
			return false;
		}
		return true;
	}

	private void resetarCamposEnvio(){
		txtChat.setText("");
		txtChat.requestFocus();
	}
	
}
