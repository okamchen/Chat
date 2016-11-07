package br.feevale.service;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

import org.json.JSONException;
import org.json.JSONObject;

import br.feevale.util.ProtocoloUtil;

public class ChatService extends JFrame {
	
	ArrayList clientOutputStreams;
	List<String> usuarios = new ArrayList<String>();

	private JButton btnLimpar;
	private JButton btnFim;
	private JButton btnInicio;
	private JButton btnUsuarios;
	private JScrollPane jScrollPane1;
	private JTextArea taChat;

	public class ClientHandler implements Runnable {
		BufferedReader reader;
		Socket sock;
		PrintWriter client;

		public ClientHandler(Socket clientSocket, PrintWriter user) {
			client = user;
			try {
				sock = clientSocket;
				InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
				reader = new BufferedReader(isReader);
			} catch (Exception ex) {
				taChat.append("Error... \n");
			}

		}

		@Override
		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					JSONObject msgJson = ProtocoloUtil.converterMsgJson(message);

					if (!usuarios.contains(msgJson.get("Usuario"))) {
						userAdd(msgJson);
					} 
					enviarParaClientes(msgJson);
				}
			} catch (Exception ex) {
				taChat.append("Perda de conexao. \n");
				ex.printStackTrace();
				clientOutputStreams.remove(client);
			}
		}
	}

	public ChatService() {
		initComponents();
	}

	private void initComponents() {

		jScrollPane1 = new JScrollPane();
		taChat = new JTextArea();
		btnInicio = new JButton();
		btnFim = new JButton();
		btnUsuarios = new JButton();
		btnLimpar = new JButton();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Chat - Server's frame");
		setName("server"); // NOI18N
		setResizable(false);

		taChat.setColumns(20);
		taChat.setRows(5);
		jScrollPane1.setViewportView(taChat);

		btnInicio.setText("Iniciar");
		btnInicio.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnInicioActionPerformed(evt);
			}
		});

		btnFim.setText("Fim");
		btnFim.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnFimActionPerformed(evt);
			}
		});

		btnUsuarios.setText("Usuarios Online");
		btnUsuarios.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnUsuariosActionPerformed(evt);
			}
		});

		btnLimpar.setText("Limpar");
		btnLimpar.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnLimparActionPerformed(evt);
			}
		});

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout
						.createSequentialGroup().addContainerGap().addGroup(layout
								.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
										jScrollPane1)
								.addGroup(layout.createSequentialGroup()
										.addGroup(layout
												.createParallelGroup(GroupLayout.Alignment.LEADING, false)
												.addComponent(btnFim, GroupLayout.DEFAULT_SIZE,
														GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(btnInicio, GroupLayout.DEFAULT_SIZE, 75,
														Short.MAX_VALUE))
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 291,
												Short.MAX_VALUE)
										.addGroup(layout
												.createParallelGroup(GroupLayout.Alignment.LEADING, false)
												.addComponent(btnLimpar, GroupLayout.DEFAULT_SIZE,
														GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(btnUsuarios, GroupLayout.DEFAULT_SIZE, 103,
														Short.MAX_VALUE))))
						.addContainerGap())
				.addGroup(GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGap(209, 209, 209)));
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								layout.createSequentialGroup().addContainerGap()
										.addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 340,
												Short.MAX_VALUE)
										.addGap(18, 18, 18)
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(btnInicio).addComponent(btnUsuarios))
										.addGap(18, 18, 18)
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(btnLimpar).addComponent(btnFim))
										.addGap(4, 4, 4)));

		pack();
	}

	private void btnFimActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_b_endActionPerformed
		try {
			Thread.sleep(5000); // 5000 milliseconds is five second.
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		log("Server: est� parado e todos os usu�rios est�o desconectados.\n:Chat");
		taChat.append("Parando Server... \n");

		taChat.setText("");
	}

	private void btnInicioActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_b_startActionPerformed
		Thread starter = new Thread(new ServerStart());
		starter.start();

		taChat.append("Servidor iniciado...\n");
	}

	private void btnUsuariosActionPerformed(java.awt.event.ActionEvent evt) {
		taChat.append("\n Usu�rios online : \n");
		for (String current_user : usuarios) {
			taChat.append(current_user);
			taChat.append("\n");
		}

	}

	private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {
		taChat.setText("");
	}

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ChatService().setVisible(true);
			}
		});
	}

	public class ServerStart implements Runnable {
		@Override
		public void run() {
			clientOutputStreams = new ArrayList();
			try {
				ServerSocket serverSock = new ServerSocket(2222);
				while (true) {
					Socket clientSock = serverSock.accept();
					PrintWriter writer = new PrintWriter(clientSock.getOutputStream());
					clientOutputStreams.add(writer);

					Thread listener = new Thread(new ClientHandler(clientSock, writer));
					listener.start();
					taChat.append("Feita conexao. \n");
				}
			} catch (Exception ex) {
				taChat.append("Error ao conectar. \n");
			}
		}
	}

	public void userAdd(JSONObject json) throws JSONException {
		String usuario = json.get("Usuario").toString();
		usuarios.add(usuario);
		JSONObject msg = ProtocoloUtil.montaMsgJson(usuario, "Usuario " + usuario + " conectado");
		enviarParaClientes(msg);
	}

	public void userRemove(String data) {
//		String message, add = ": Conectado", done = "Server: :Done", name = data;
//		usuarios.remove(name);
//		String[] tempList = new String[(usuarios.size())];
//		usuarios.toArray(tempList);
//
//		for (String token : tempList) {
//			message = (token + add);
//			log(message);
//		}
//		log(done);
	}

	public void log(String message) {
		taChat.append("Registro: " + message + "\n");
		taChat.setCaretPosition(taChat.getDocument().getLength());
	}
	
	public void enviarParaClientes(JSONObject json) {
		Iterator it = clientOutputStreams.iterator();
		log(json.toString());
		
		while (it.hasNext()) {
			try {
				PrintWriter writer = (PrintWriter) it.next();
//				if(!usuarios.contains(json.get("Usuario"))){
					writer.println(json.toString());
//				}
				writer.flush();
				
			} catch (Exception ex) {
				taChat.append("Error ao escrever o log. \n");
			}
		}
	}

}
