package com;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class raw {
	public String RF_PORT_ID;
	public String City, Gu, Dong;
	public String RF_POT_X, RF_POT_Y;
	public double LTE_RSRP_AVG, LTE_SINR_AVG, LTE_TX_PWR_AVG;
	public int LTE, LTE_PF_Pot;
	public double Latitude, Longitude;
	public raw(String RF_PORT_ID, String City, String Gu, String Dong,
					String RF_POT_X, String RF_POT_Y, String LTE_RSRP_AVG, String LTE_SINR_AVG, String LTE_TX_PWR_AVG,
					String LTE, String LTE_PF_Pot){
		this.RF_PORT_ID = RF_PORT_ID;
		this.City = City;
		this.Gu = Gu;
		this.Dong = Dong;
		this.RF_POT_X = RF_POT_X;
		this.RF_POT_Y = RF_POT_Y;
		this.LTE_RSRP_AVG = Double.parseDouble(LTE_RSRP_AVG);
		this.LTE_SINR_AVG = Double.parseDouble(LTE_SINR_AVG);
		this.LTE_TX_PWR_AVG = Double.parseDouble(LTE_TX_PWR_AVG);
		
		String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
		this.LTE = (int)Double.parseDouble(LTE.replaceAll(match, ""));
		this.LTE_PF_Pot = (int)Double.parseDouble(LTE_PF_Pot);
		T_GPS();
	}
	private void T_GPS() {
		double a = 6378137, e = 0.0818192, eisq = 0.0067395, k0 = 0.9996;
		double C1 = 0.00251883, C2 = 0.00000370094, C3 = 0.00000000744, C4 = 0.000000000017;
		double F3 = Double.parseDouble(RF_POT_X), G3 = Double.parseDouble(RF_POT_Y), UTM_X = 203.00, UTM_Y = 759.00;
		double East_Prime = 500000-F3+UTM_X;
		double Arc_Length = (G3+UTM_Y)/k0;
		double mu = Arc_Length/(a*(1-Math.pow(e, 2)/4-3*Math.pow(e, 4)/64-5*Math.pow(e, 6)/256));
		double phi = mu+C1*Math.sin(2*mu)+C2*Math.sin(4*mu)+C3*Math.sin(6*mu)+C4*Math.sin(8*mu);
		double CC1 = eisq*Math.pow(Math.cos(phi), 2);
		double T1 = Math.pow(Math.tan(phi), 2);
		double N1 = a/Math.pow(1-Math.pow(e*Math.sin(phi), 2), 0.5);
		double R1 = a*(1-e*e)/Math.pow((1-Math.pow(e*Math.sin(phi),2)),1.5);
		double D = East_Prime/(N1*k0);
		double Fact1 = N1*Math.tan(phi)/R1, Fact2 = D*D/2;
		double Fact3 = (5+3*T1+10*CC1-4*CC1*CC1-9*eisq)*Math.pow(D,4)/24;
		double Fact4 =(61+90*T1+298*CC1+45*T1*T1-252*eisq-3*CC1*CC1)*Math.pow(D, 6)/720;
		double LoFact1 = D, LoFact2 = (1+2*T1+CC1)*Math.pow(D, 3)/6;
		double LoFact3 = (5-2*CC1+28*T1-3*CC1*CC1+8*eisq+24*T1*T1)*Math.pow(D, 5)/120;
		double Delta_Long = (LoFact1-LoFact2+LoFact3)/Math.cos(phi), Zone_CM = 6*(52)-183;
		double Latitude = 180*(phi-Fact1*(Fact2+Fact3+Fact4))/Math.PI, Longitude = Zone_CM-Delta_Long*180/Math.PI;
		NumberFormat df = new DecimalFormat("#.000000");
		this.Latitude = Double.parseDouble(df.format(Latitude));
		this.Longitude = Double.parseDouble(df.format(Longitude));
	}
}
