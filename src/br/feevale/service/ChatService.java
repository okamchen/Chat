package br.feevale.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

public class ChatService extends JFrame {
	
	ArrayList clientOutputStreams;
	ArrayList<String> users;

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
			String message, connect = "Connect", disconnect = "Disconnect", chat = "Chat";
			String[] data;

			try {
				while ((message = reader.readLine()) != null) {
					taChat.append("Recebido: " + message + "\n");
					data = message.split(":");

					for (String token : data) {
						taChat.append(token + "\n");
					}

					if (data[2].equals(connect)) {
						log((data[0] + ":" + data[1] + ":" + chat));
						userAdd(data[0]);
					} else if (data[2].equals(disconnect)) {
						log((data[0] + ": está desconectado." + ":" + chat));
						userRemove(data[0]);
					} else if (data[2].equals(chat)) {
						log(message);
					} else {
						taChat.append("Nenhum condição encontrada. \n");
					}
				}
			} catch (Exception ex) {
				taChat.append("Perda de conexão. \n");
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
				b_startActionPerformed(evt);
			}
		});

		btnFim.setText("Fim");
		btnFim.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				b_endActionPerformed(evt);
			}
		});

		btnUsuarios.setText("Usuários Online");
		btnUsuarios.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				b_usersActionPerformed(evt);
			}
		});

		btnLimpar.setText("Limpar");
		btnLimpar.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				b_clearActionPerformed(evt);
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

	private void b_endActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_b_endActionPerformed
		try {
			Thread.sleep(5000); // 5000 milliseconds is five second.
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		log("Server: está parado e todos os usuários estão desconectados.\n:Chat");
		taChat.append("Parando Server... \n");

		taChat.setText("");
	}

	private void b_startActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_b_startActionPerformed
		Thread starter = new Thread(new ServerStart());
		starter.start();

		taChat.append("Servidor iniciado...\n");
	}

	private void b_usersActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_b_usersActionPerformed
		taChat.append("\n Usuários online : \n");
		for (String current_user : users) {
			taChat.append(current_user);
			taChat.append("\n");
		}

	}

	private void b_clearActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_b_clearActionPerformed
		taChat.setText("");
	}

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
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
			users = new ArrayList<String>();

			try {
				ServerSocket serverSock = new ServerSocket(2222);
				while (true) {
					Socket clientSock = serverSock.accept();
					PrintWriter writer = new PrintWriter(clientSock.getOutputStream());
					clientOutputStreams.add(writer);

					Thread listener = new Thread(new ClientHandler(clientSock, writer));
					listener.start();
					taChat.append("Feita conexão. \n");
				}
			} catch (Exception ex) {
				taChat.append("Error ao conectar. \n");
			}
		}
	}

	public void userAdd(String data) {
		String message, add = ": :Conectado", done = "Server: :Done", name = data;
		users.add(name);
		String[] tempList = new String[(users.size())];
		users.toArray(tempList);

		for (String token : tempList) {
			message = (token + add);
			log(message);
		}
		log(done);
	}

	public void userRemove(String data) {
		String message, add = ": Conectado", done = "Server: :Done", name = data;
		users.remove(name);
		String[] tempList = new String[(users.size())];
		users.toArray(tempList);

		for (String token : tempList) {
			message = (token + add);
			log(message);
		}
		log(done);
	}

	public void log(String message) {
		Iterator it = clientOutputStreams.iterator();

		while (it.hasNext()) {
			try {
				PrintWriter writer = (PrintWriter) it.next();
				writer.println(message);
				taChat.append("Enviado: " + message + "\n");
				writer.flush();
				taChat.setCaretPosition(taChat.getDocument().getLength());

			} catch (Exception ex) {
				taChat.append("Error ao escrever o log. \n");
			}
		}
	}

}
