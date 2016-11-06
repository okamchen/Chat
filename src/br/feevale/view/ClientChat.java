package br.feevale.view;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

import br.feevale.bibliotecas.*; 

public class ClientChat extends JFrame {

	String usuario, url = "localhost";
	ArrayList<String> usuarios = new ArrayList<String>();
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

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ClientChat().setVisible(true);
			}
		});
	}

	public void ListenThread() {
		Thread IncomingReader = new Thread(new IncomingReader());
		IncomingReader.start();
	}

	public void addUsuario(String data) {
		usuarios.add(data);
	}

	public void removerUsuario(String usuario) {
		taChat.append(usuario + " está offline.\n");
	}

	public void writeUsers() {
		String[] tempList = new String[(usuarios.size())];
		usuarios.toArray(tempList);
	}

	public void enviarDesconexao() {
		String bye = (usuario + ": :Disconectou");
		try {
			writer.println(bye);
			writer.flush();
		} catch (Exception e) {
			taChat.append("Erro ao enviar mensagem de desconexão.\n");
		}
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
			String[] data;
			String stream, done = "Done", connect = "Conectado", disconnect = "Desconectado", chat = "Chat";

			try {
				while ((stream = reader.readLine()) != null) {
					data = stream.split(":");
					
					if (data[2].equals(chat)) {
						taChat.append(data[0] + ": " + data[1] + "\n");
						taChat.setCaretPosition(taChat.getDocument().getLength());
					} else if (data[2].equals(connect)) {
						taChat.removeAll();
						addUsuario(data[0]);
					} else if (data[2].equals(disconnect)) {
						removerUsuario(data[0]);
					} else if (data[2].equals(done)) {
						writeUsers();
						usuarios.clear();
					}
				}
			} catch (Exception ex) {
				System.out.print("Error" + ex.getMessage());
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

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Chat Java");
		setResizable(false);
		
		lblUrl.setText("URL : ");
		txtUrl.setText("localhost");
		txtUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUrlActionPerformed(evt);
            }
        });

		lblPorta.setText("Porta :");
		txtPorta.setText("2222");
		txtPorta.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPortaActionPerformed(evt);
            }
        });

		lblUsuario.setText("Usuário :");
		txtUsuario.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsuarioActionPerformed(evt);
            }
        });

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
				writer.println(usuario + ": está conectado.:Connect");
				writer.flush();
				isConnected = true;
			} catch (Exception ex) {
				taChat.append("Erro de conexão! Tente novamente. \n");
				txtUsuario.setEditable(true);
			}

			ListenThread();

		} else if (isConnected == true) {
			taChat.append("Você já está conectado. \n");
		}
	}

	private void disconectar(ActionEvent evt) {
		enviarDesconexao();
		desconectar();
	}

	private void enviarMsg(ActionEvent evt) throws JSONException {
	
		String nothing = "";
		if ((txtChat.getText()).equals(nothing)) {
			txtChat.setText("");
			txtChat.requestFocus();
			System.out.printf("nothing");
		} else {
			try {
				
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				JSONObject JsonMensagem = new JSONObject();
				JSONObject arquivo = new JSONObject();
				
				if (txtChat.getText() != null) {
					JsonMensagem.put("Mensagem", txtChat.getText());
					arquivo.put("Nome", "");
					arquivo.put("Conteudo", "");
					arquivo.put("Tipo", "");
				} else {
					// Adiciona informaçoes do arquivo.
					JsonMensagem.put("Mensagem", "");
				}
				
				JsonMensagem.put("DataHora", dateFormat.format(cal.get(Calendar.HOUR_OF_DAY)));
				JsonMensagem.put("Usuario", usuario);
				JsonMensagem.put("Arquivo", arquivo);
				
				
//				/System.out.println( "-> " + JsonMesangem.toString() + " <-");
				 
				writer.println(JsonMensagem.get("Usuario") + " diz:" + JsonMensagem.get("Mensagem") + ":" + "Chat");
				writer.flush();
				
				//System.out.printf("Envia mensagem");
				
			} catch (Exception ex) {
				taChat.append("Erro ao enviar mensagem. \n");
			}
			txtChat.setText("");
			txtChat.requestFocus();
		}

		txtChat.setText("");
		txtChat.requestFocus();
	}
	
	private void txtUrlActionPerformed(java.awt.event.ActionEvent evt) {
	   
	}

    private void txtPortaActionPerformed(ActionEvent evt) {
    	   
    }

    private void txtUsuarioActionPerformed(ActionEvent evt) {
    
    }



}
