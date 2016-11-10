package br.feevale.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ProtocoloUtil {

	public static JSONObject montaMsgJson(String usuario, String msg, String pathArq) {

		JSONObject obj = new JSONObject();
		JSONObject arq = new JSONObject();
		try {
			if(pathArq != null){
				
				arq.put( "Nome", getNameArq(pathArq) );
				arq.put( "Conteudo", getBytesArq(pathArq));
				arq.put( "Tipo", getTypeArq(pathArq));
				obj.put( "Arquivo", arq );
			}
			obj.put( "Mensagem", msg );
			obj.put( "DataHora",  obterDataHoraFormatada());		
			obj.put( "Usuario", usuario );
		} catch (JSONException e) {
			System.out.println("Erro ao ler JSON");
		} catch (IOException e) {
			System.out.println("Erro ao tentar obter array de byte de arquivo");
		}
		
		return obj;
	}
	
	private static String getTypeArq(String pathArq) {
		return pathArq.substring(pathArq.lastIndexOf(".") + 1);
	}

	private static String getBytesArq(String pathArq) throws IOException {
		File file = new File(pathArq); 
		byte[] bFile = new byte[(int) file.length()];
		FileInputStream fileInputStream = new FileInputStream(file);
		fileInputStream.read(bFile);
		fileInputStream.close();
		return bFile.toString();
	}

	private static String getNameArq(String pathArq) {
		return pathArq.substring(pathArq.lastIndexOf("/") + 1).replace("."+getTypeArq(pathArq), "");
	}

	public static JSONObject montaMsgJson(String usuario, String msg) {
		return montaMsgJson(usuario, msg, null);
	}

	private static String obterDataHoraFormatada() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.get(Calendar.HOUR_OF_DAY));
	}

	public static JSONObject converterMsgJson(String message) {

		JSONObject obj = new JSONObject();
		try {
			obj = new JSONObject(message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

}
