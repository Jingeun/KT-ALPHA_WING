package com;

import java.util.LinkedList;

public class data {
	//RawData.get(i), TargetData.get(i), RSRP_SUM, RSPR_CNT, SINR_SUM, SINR_CNT, TX_PWR_SUM, TXPWR_CNT, LTE_SUM, LTE_CNT, RFPot_SUM, RFPot_CNT);
	public LinkedList<raw> raw;
	public target target;
	public double T_Latitude = 0, T_longitude = 0;
	public double RSRP_SUM = 0, SINR_SUM = 0, TXPWR_SUM = 0;
	public long RSPR_CNT = 0, SINR_CNT = 0, TXPWR_CNT = 0;
	public int LTE_SUM = 0, RFPot_SUM = 0, LTE_CNT = 0, RFPot_CNT = 0;
	public double RSRP_AVG = 0, SINR_AVG = 0, TXPWR_AVG = 0, LTE_AVG = 0;
	
	public data(LinkedList<raw> RawData, target TargetData, double RSRP_SUM, long RSPR_CNT, double SINR_SUM, long SINR_CNT, 
			    double TXPWR_SUM, long TXPWR_CNT, int LTE_SUM, int LTE_CNT, int RFPot_SUM, int RFPot_CNT){
		this.raw = RawData;
		this.target = TargetData;
		this.RSRP_SUM = RSRP_SUM;
		this.RSPR_CNT = RSPR_CNT;
		this.SINR_SUM = SINR_SUM;
		this.SINR_CNT = SINR_CNT;
		this.TXPWR_SUM = TXPWR_SUM;
		this.TXPWR_CNT = TXPWR_CNT;
		this.LTE_SUM = LTE_SUM;
		this.LTE_CNT = LTE_CNT;
		this.RFPot_SUM = RFPot_SUM;
		this.RFPot_CNT = RFPot_CNT;
		if(RSRP_SUM!=0 && RFPot_CNT!=0)
			RSRP_AVG = RSRP_SUM / RFPot_CNT;
		if(SINR_SUM!=0 && SINR_CNT!=0)
			SINR_AVG = SINR_SUM / SINR_CNT;
		if(TXPWR_SUM!=0 && TXPWR_CNT!=0)
			TXPWR_AVG = TXPWR_SUM / TXPWR_CNT;
		if(LTE_CNT!=0 && LTE_SUM!=0)
			LTE_AVG = LTE_SUM / LTE_CNT;
	}
	
}
