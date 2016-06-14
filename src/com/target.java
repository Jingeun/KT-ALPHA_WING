package com;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JOptionPane;

public class target {
	public String REPEATER_ID, REPEATER_NAME;
	public String City, Gu, Dong, Street, Detail, InstallPot;
	public double Latitude, Longitude;
	
	public target(String REPEATER_ID, String REPEATER_NAME, String City, String Gu, String Dong, String Street,
				  String Detail, String InstallPot, String Latitude, String Langitude){
		this.REPEATER_ID = REPEATER_ID;
		this.REPEATER_NAME = REPEATER_NAME;
		this.Detail = Detail;
		this.InstallPot = InstallPot;
		this.City = City;
		this.Gu = Gu;
		this.Dong = Dong;
		this.Street = Street;
		this.Latitude = Translate_GPS(Latitude);
		this.Longitude = Translate_GPS(Langitude);
	}
	
	public double Translate_GPS(String S){
		if(S.equals("0") || S.length()<1 || S == null || S.equals("false")) return 0;
		NumberFormat df = new DecimalFormat("#.000000");
		String T[] = S.split("-");
		double pos = 0;
		try{
			pos = Double.parseDouble(T[0]) + (Double.parseDouble(T[1])/(double)60) + (Double.parseDouble(T[2])/(double)3600);
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "중계기ID "+this.REPEATER_ID+"의 위경도가 정상적인지 확인해주세요.", "Error", JOptionPane.WARNING_MESSAGE);
			System.exit(1);
		}
		return Double.parseDouble(df.format(pos));
	}
}
