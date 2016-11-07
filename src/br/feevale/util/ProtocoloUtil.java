package br.feevale.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ProtocoloUtil {

	public static JSONObject montaMsgJson(String usuario, String msg, Byte[] bytes) {

		JSONObject obj = new JSONObject();
		JSONObject arq = new JSONObject();
		try {
			
			if(bytes != null){
				arq.put( "Nome", "" );
				arq.put( "Conteudo", "" );
				arq.put( "Tipo", "" );
				obj.put( "Arquivo", arq );
			}
			
			obj.put( "Mensagem", msg );
			obj.put( "DataHora",  obterDataHoraFormatada());		
			obj.put( "Usuario", usuario );
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return obj;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

}
