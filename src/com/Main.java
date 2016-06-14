package com;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.opencsv.CSVWriter;

// 코드 난잡...
public class Main {
	public ArrayList<raw> RawData;
	private ArrayList<target> TargetData;
	private Map<String, data> SearchData;
	private Map<String, String> SearchData_NAME;
	private String RawFileName, TargetFileName;
	private static JFrame jf;
	boolean RawBoolean, TargetBoolean;
	JPanel dataPanel1, dataPanel2, distancePanel, checkboxPanel, jbtnPanel, resultPanel, resultQueryPanel, mapPanel;
	JLabel jl2, query;
	JProgressBar rawBar, targetBar, searchBar, excelBar;
	JTextField distanceJT, resultSearchTF;
	JCheckBox checkbox, mapCheckbox, RSRPcheck, SINRcheck, RFPotcheck;
	JTable resultTable;
	Dimension screenSize;
	private double distance;
	private map mapJF;
	private static Thread t1, t2;
	private String RSRPmin, RSRPmax, SINRmin, SINRmax, RFPotmin, RFPotmax, ExceptTargetStr[], ExceptPotStr[];
	public Main(){
		init();
	}
	private void init(){
		RawData = new ArrayList<raw>();
		TargetData = new ArrayList<target>();
		SearchData = new HashMap<String, data>();
		SearchData_NAME = new HashMap<String, String>();
		t1 = new Thread(new SearchThread());
		t2 = new Thread(new MyThread());
		jf = new JFrame("데이터 분석");
		mapJF = new map();
		RawBoolean = false;
		TargetBoolean = false;
		RSRPmin = RSRPmax = null;
		SINRmin = SINRmax = null;
		RFPotmin = RFPotmax = null;
		ExceptTargetStr = new String[4];
		ExceptPotStr = new String[4];
	}
	private void GUI() {
		//GUI...
		JFrame Jf = new JFrame("ALPHA WiNG v1.6");
		Jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Jf.setSize(screenSize.width*2/3, screenSize.height*3/4);
		Jf.setLayout(new BorderLayout(10, 10));
		
		JPanel top = GUI_TOP();
		JPanel west = new JPanel();
		west.setLayout(new BoxLayout(west, BoxLayout.Y_AXIS));
		west.setBorder(new CompoundBorder(new EmptyBorder(0, 10, 5, 2), 
				BorderFactory.createTitledBorder(new BevelBorder(BevelBorder.RAISED), "ALPHA WiNG 데이터 입력")));
		
		JPanel dataPanel = GUI_Search_Border();
		dataPanel.setBorder(new CompoundBorder(new EmptyBorder(3, 5, 10, 5),
				BorderFactory.createTitledBorder("데이터")));
		
		resultPanel = new JPanel(new GridLayout(2,1));
		resultPanel.setBorder(new CompoundBorder(new EmptyBorder(3, 5, 0, 5),
				BorderFactory.createTitledBorder("검색 조건")));
		resultQueryPanel = new JPanel();
		JLabel resultSearchLabel = new JLabel("ID 또는 국소명 : ");
		resultSearchLabel.setFont(new Font("Times", Font.BOLD, 15));
		resultQueryPanel.add(resultSearchLabel);
		resultSearchTF = new JTextField(10);
		resultSearchTF.setFont(new Font("Times", Font.BOLD, 15));
		resultSearchTF.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			public void keyPressed(KeyEvent e) { if(e.getKeyCode()==KeyEvent.VK_ENTER){ Search_ActionListener(); } }
		});
		resultQueryPanel.add(resultSearchTF);
		JButton resultButton = new JButton("검색");
		resultButton.setFont(new Font("Times", Font.BOLD, 13));
		resultButton.setPreferredSize(new Dimension(65, 30));
		//실행 결과 액션리스너
		resultButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent arg0) { Search_ActionListener(); } });
		resultQueryPanel.add(resultButton);
		
		mapPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		mapCheckbox = new JCheckBox("지도 보기");
		mapCheckbox.setSelected(true);
		mapCheckbox.setFont(new Font("Times", Font.BOLD, 13));
		mapPanel.add(mapCheckbox);
		resultPanel.add(mapPanel);
		resultPanel.add(resultQueryPanel);
		west.add(dataPanel);
		west.add(resultPanel);
		
		JPanel table = new JPanel(new BorderLayout());
		table.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 5, 10), 
				BorderFactory.createTitledBorder("검색 테이블")));
		
		String col[] = {"번호", "ID", "국소명", "Pot_ID", "거리", "RSRP", "SINR", "TXPWR", "측정수", };
		DefaultTableModel tableModel = new DefaultTableModel(col, 0);
		resultTable = new JTable(tableModel);
		int colWidth[] = {40, 140, 200, 240, 80, 80,80,85,80};
		for(int i=0;i<colWidth.length;i++)
			resultTable.getColumnModel().getColumn(i).setPreferredWidth(colWidth[i]);
		resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		resultTable.setFont(new Font("Times", Font.PLAIN, 13));
		resultTable.setRowHeight(22);
		resultTable.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseClicked(MouseEvent arg0) {
				if(!mapJF.isActive())mapJF.loadClickEvent(resultTable.getSelectedRow());
			}
		});
		JScrollPane scollPane = new JScrollPane(resultTable);
		table.add(scollPane, BorderLayout.CENTER);
		Jf.add(top, BorderLayout.NORTH);
		Jf.add(west, BorderLayout.WEST);
		Jf.add(table, BorderLayout.CENTER);
		Jf.setVisible(true);
	}
	private void Search_ActionListener() {
		DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
		
		String str = resultSearchTF.getText().trim();
		if(SearchData.containsKey(str) && SearchData.get(str).raw.size()>0
				|| SearchData.containsKey(SearchData_NAME.get(str)) && SearchData.get(SearchData_NAME.get(str)).raw.size()>0){
			model.setNumRows(0);
			data d = SearchData.get(SearchData.containsKey(str)?str:SearchData_NAME.get(str));
			Export_JTable(model, d.target, d.raw);
			if(mapCheckbox.isSelected())
				Visible_Map(d.target, d.raw);
		}else{ JOptionPane.showMessageDialog(null, "검색 결과가 없습니다."); }
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer(); // 테이블셀 렌더러 객체를 생성.
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		for(int i=0;i<resultTable.getColumnCount();i++)
			resultTable.getColumnModel().getColumn(i).setCellRenderer(dtcr);
	}
	private void Visible_Map(target t, LinkedList<raw> r) {
		if(!mapJF.isActive()){
			mapJF = new map();
			mapJF.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			mapJF.setSize(screenSize.width/3, (screenSize.height*3/4)-95);
			mapJF.setVisible(true);
		}
		mapJF.loadURL(screenSize, t, r);
	}
	private void Export_JTable(DefaultTableModel model, target t, LinkedList<raw> r) {
		String[] dd = new String[9];
		NumberFormat df = new DecimalFormat("#.000");
		for(int i=0;i<r.size();i++){
			raw rawCell = r.get(i);
			dd[0] = String.valueOf(i+1);
			dd[1] = t.REPEATER_ID;
			dd[2] = t.REPEATER_NAME;
			dd[3] = rawCell.RF_PORT_ID;
			dd[4] = df.format(Distance_Calc(t.Latitude, t.Longitude, rawCell.Latitude, rawCell.Longitude));
			dd[5] = Double.toString(rawCell.LTE_RSRP_AVG);
			dd[6] = Double.toString(rawCell.LTE_SINR_AVG);
			dd[7] = Double.toString(rawCell.LTE_TX_PWR_AVG);
			dd[8] = Integer.toString(rawCell.LTE);
			model.addRow(dd);
		}
	}
	private JPanel GUI_Search_Border() {
		JPanel search = new JPanel();
		search.setLayout(new GridLayout(10,1,0,10));
		//대상국소 데이터
		dataPanel1 = new JPanel();
		JLabel jl1 = newLabel("대상국소", Font.BOLD, 16, Color.LIGHT_GRAY, 110);
		jl1.setForeground(Color.blue);
		JTextField rawTextField1 = newTextfield(18, Font.PLAIN, 13);
		JButton rawBtn1 = newJButton(" ... ");
		rawBtn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FileDialog fileD = openFile();
				TargetFileName = fileD.getDirectory() + fileD.getFile();
				rawTextField1.setText(fileD.getFile());
			}
		});
		dataPanel1.add(jl1);
		dataPanel1.add(rawTextField1);
		dataPanel1.add(rawBtn1);
		
		dataPanel2 = new JPanel();
		jl2 = newLabel("WiNG Data", Font.BOLD, 16, Color.LIGHT_GRAY, 110);
		jl2.setForeground(Color.blue);
		JTextField rawTextField2 = newTextfield(18, Font.PLAIN, 13);
		JButton rawBtn2 = newJButton(" ... ");
		rawBtn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FileDialog fileD = openFile();
				RawFileName = fileD.getDirectory() + fileD.getFile();
				rawTextField2.setText(fileD.getFile());
			}
		});
		dataPanel2.add(jl2);
		dataPanel2.add(rawTextField2);
		dataPanel2.add(rawBtn2);
		
		distancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel distanceLabel = newLabel("측정 반경", Font.BOLD, 13, Color.gray, 85);
		JRadioButton c1 = new JRadioButton("50m");
		c1.setSelected(true);
		JRadioButton c2 = new JRadioButton("100m");
		JRadioButton c3 = new JRadioButton("200m");
		JRadioButton c4 = new JRadioButton();
		c1.setFont(new Font("Times", Font.BOLD, 13));
		c2.setFont(new Font("Times", Font.BOLD, 13));
		c3.setFont(new Font("Times", Font.BOLD, 13));
		c4.setFont(new Font("Times", Font.BOLD, 13));
		ButtonGroup cbg = new ButtonGroup();
		cbg.add(c1);cbg.add(c2);cbg.add(c3);cbg.add(c4);
		
		distanceJT = new JTextField(3);
		distanceJT.setEditable(false);
		distanceJT.setFont(new Font("Times", Font.BOLD, 15));
		JLabel distanceM = new JLabel("m");
		c1.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {distanceJT.setEditable(false);}});
		c2.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {distanceJT.setEditable(false);}});
		c3.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {distanceJT.setEditable(false);}});
		c4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				distanceJT.setEditable(true);
			}
		});
		distancePanel.add(distanceLabel);
		distancePanel.add(c1);distancePanel.add(c2);distancePanel.add(c3);distancePanel.add(c4);
		distancePanel.add(distanceJT);
		distancePanel.add(distanceM);
		
		checkboxPanel = new JPanel();
		checkboxPanel.setPreferredSize(new Dimension(checkboxPanel.getWidth(), 20));
		checkbox = new JCheckBox("Excel 데이터 저장");
		checkbox.setFont(new Font("Times", Font.BOLD, 15));
		checkbox.setSelected(true);
		checkboxPanel.add(checkbox);
		
		JPanel RSRP = new JPanel();
		RSRPcheck = new JCheckBox(" RSRP :");
		JTextField RSRPmintext = new JTextField(3);
		JLabel RSRPlabel = new JLabel(" ≤  RSRP＜  ");
		JTextField RSRPmaxtext = new JTextField(3);
		RSRP.add(RSRPcheck);RSRP.add(RSRPmintext);RSRP.add(RSRPlabel);RSRP.add(RSRPmaxtext);
		RSRPmintext.setEditable(false);
		RSRPmaxtext.setEditable(false);
		RSRPcheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(RSRPcheck.isSelected()){
					RSRPmintext.setEditable(true);
					RSRPmaxtext.setEditable(true);
				}else{
					RSRPmintext.setEditable(false);
					RSRPmaxtext.setEditable(false);
				}
			}
		});
		
		JPanel SINR = new JPanel();
		SINRcheck = new JCheckBox(" SINR :");
		JTextField SINRmintext = new JTextField(3);
		JLabel SINRlabel = new JLabel(" ≤   SINR ＜  ");
		JTextField SINRmaxtext = new JTextField(3);
		SINRmintext.setEditable(false);
		SINRmaxtext.setEditable(false);
		SINRcheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(SINRcheck.isSelected()){
					SINRmintext.setEditable(true);
					SINRmaxtext.setEditable(true);
				}else{
					SINRmintext.setEditable(false);
					SINRmaxtext.setEditable(false);
				}
			}
		});
		SINR.add(SINRcheck); SINR.add(SINRmintext); SINR.add(SINRlabel); SINR.add(SINRmaxtext);
		
		JPanel RFPot = new JPanel();
		RFPotcheck = new JCheckBox(" RFPot :");
		JTextField RFPotmintext = new JTextField(3);
		JLabel RFPotlabel = new JLabel(" ≤  RFPot ＜ ");
		JTextField RFPotmaxtext = new JTextField(3);
		RFPotmintext.setEditable(false);
		RFPotmaxtext.setEditable(false);
		RFPotcheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(RFPotcheck.isSelected()){
					RFPotmintext.setEditable(true);
					RFPotmaxtext.setEditable(true);
				}else{
					RFPotmintext.setEditable(false);
					RFPotmaxtext.setEditable(false);
				}
			}
		});
		RFPot.add(RFPotcheck); RFPot.add(RFPotmintext); RFPot.add(RFPotlabel); RFPot.add(RFPotmaxtext);
		
		for(Component c : RSRP.getComponents()){ c.setFont(new Font("Times", Font.BOLD, 15)); }
		for(Component c : SINR.getComponents()){ c.setFont(new Font("Times", Font.BOLD, 15)); }
		for(Component c : RFPot.getComponents()){ c.setFont(new Font("Times", Font.BOLD, 15)); }
		
		JPanel ExceptTarget = new JPanel();
		JCheckBox ExceptTargetBox = new JCheckBox(" 대상(중계기명) 제외 : ");
		ExceptTargetBox.setFont(new Font("Times", Font.BOLD, 13));
		JTextField ExceptTargetJText1 = new JTextField(4);
		ExceptTargetJText1.setFont(new Font("Times", Font.BOLD, 15));
		ExceptTargetJText1.setEditable(false);
		JTextField ExceptTargetJText2 = new JTextField(4);
		ExceptTargetJText2.setFont(new Font("Times", Font.BOLD, 15));
		ExceptTargetJText2.setEditable(false);
		JTextField ExceptTargetJText3 = new JTextField(4);
		ExceptTargetJText3.setFont(new Font("Times", Font.BOLD, 15));
		ExceptTargetJText3.setEditable(false);
		JTextField ExceptTargetJText4 = new JTextField(4);
		ExceptTargetJText4.setFont(new Font("Times", Font.BOLD, 15));
		ExceptTargetJText4.setEditable(false);
		ExceptTarget.add(ExceptTargetBox);
		ExceptTarget.add(ExceptTargetJText1);
		ExceptTarget.add(ExceptTargetJText2);
		ExceptTarget.add(ExceptTargetJText3);
		ExceptTarget.add(ExceptTargetJText4);
		ExceptTargetBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExceptTargetJText1.setEditable(ExceptTargetBox.isSelected()?true:false);
				ExceptTargetJText2.setEditable(ExceptTargetBox.isSelected()?true:false);
				ExceptTargetJText3.setEditable(ExceptTargetBox.isSelected()?true:false);
				ExceptTargetJText4.setEditable(ExceptTargetBox.isSelected()?true:false);
			}
		});
		
		JPanel ExceptPot = new JPanel();
		JCheckBox ExceptPotBox = new JCheckBox(" 설치장소 제외 : ");
		ExceptPotBox.setFont(new Font("Times", Font.BOLD, 13));
		JTextField ExceptPotJText1 = new JTextField(4);
		ExceptPotJText1.setFont(new Font("Times", Font.BOLD, 15));
		ExceptPotJText1.setEditable(false);
		JTextField ExceptPotJText2 = new JTextField(4);
		ExceptPotJText2.setFont(new Font("Times", Font.BOLD, 15));
		ExceptPotJText2.setEditable(false);
		JTextField ExceptPotJText3 = new JTextField(4);
		ExceptPotJText3.setFont(new Font("Times", Font.BOLD, 15));
		ExceptPotJText3.setEditable(false);
		JTextField ExceptPotJText4 = new JTextField(4);
		ExceptPotJText4.setFont(new Font("Times", Font.BOLD, 15));
		ExceptPotJText4.setEditable(false);
		ExceptPotJText1.setEditable(false);
		ExceptPotJText2.setEditable(false);
		ExceptPotJText3.setEditable(false);
		ExceptPotJText4.setEditable(false);
		
		ExceptPotBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(ExceptPotBox.isSelected()){
					ExceptPotJText1.setEditable(true);
					ExceptPotJText2.setEditable(true);
					ExceptPotJText3.setEditable(true);
					ExceptPotJText4.setEditable(true);
				}else{
					ExceptPotJText1.setEditable(false);
					ExceptPotJText2.setEditable(false);
					ExceptPotJText3.setEditable(false);
					ExceptPotJText4.setEditable(false);
				}
			}
		});
		ExceptPot.add(ExceptPotBox);
		ExceptPot.add(ExceptPotJText1);
		ExceptPot.add(ExceptPotJText2);
		ExceptPot.add(ExceptPotJText3);
		ExceptPot.add(ExceptPotJText4);
		
		//실행 버튼
		jbtnPanel = new JPanel();
		JButton jbtn = new JButton("실 행");
		jbtn.setFont(new Font("Times", Font.BOLD, 13));
		jbtn.setPreferredSize(new Dimension(70, 28));
		jbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(RawFileName==null || RawFileName.length()<1){
					JOptionPane.showMessageDialog(null, "대상국소 데이터 파일을 넣어주세요!", "Error", JOptionPane.WARNING_MESSAGE);
				}else if(TargetFileName==null || TargetFileName.length()<1){
					JOptionPane.showMessageDialog(null, "WING Hunter 파일을 넣어주세요!", "Error", JOptionPane.WARNING_MESSAGE);
				}else if(c4.isSelected() && (distanceJT.getText()==null || distanceJT.getText().equals(""))){
					JOptionPane.showMessageDialog(null, "측정 거리를 입력해주세요!", "Error", JOptionPane.WARNING_MESSAGE);
				}else{ 
					init();
					distance = c4.isSelected()?Double.parseDouble(distanceJT.getText().trim()):c1.isSelected()?50.0:c2.isSelected()?100.0:c3.isSelected()?200.0:0;
					RSRPmin = RSRPcheck.isSelected()?RSRPmintext.getText().trim():null;
					SINRmin = SINRcheck.isSelected()?SINRmintext.getText().trim():null;
					RFPotmin = RFPotcheck.isSelected()?RFPotmintext.getText().trim():null;
					RSRPmax =  RSRPcheck.isSelected()?RSRPmaxtext.getText().trim():null;
					SINRmax = SINRcheck.isSelected()?SINRmaxtext.getText().trim():null;
					RFPotmax = RFPotcheck.isSelected()?RFPotmaxtext.getText().trim():null;
					if(ExceptTargetBox.isSelected()){
						ExceptTargetStr[0] = !ExceptTargetJText1.getText().equals("")?ExceptTargetJText1.getText().trim():null;
						ExceptTargetStr[1] = !ExceptTargetJText2.getText().equals("")?ExceptTargetJText2.getText().trim():null;
						ExceptTargetStr[2] = !ExceptTargetJText3.getText().equals("")?ExceptTargetJText3.getText().trim():null;
						ExceptTargetStr[3] = !ExceptTargetJText4.getText().equals("")?ExceptTargetJText3.getText().trim():null;
					}
					if(ExceptPotBox.isSelected()){
						ExceptPotStr[0] = !ExceptPotJText1.getText().equals("")?ExceptPotJText1.getText().trim():null;
						ExceptPotStr[1] = !ExceptPotJText1.getText().equals("")?ExceptPotJText2.getText().trim():null;
						ExceptPotStr[2] = !ExceptPotJText1.getText().equals("")?ExceptPotJText3.getText().trim():null;
						ExceptPotStr[3] = !ExceptPotJText1.getText().equals("")?ExceptPotJText4.getText().trim():null;
					}
					t2.start(); t1.start();
				}
			}
		});
		jbtnPanel.add(jbtn);
		jbtnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		search.add(dataPanel1);
		search.add(dataPanel2);
		search.add(distancePanel);
		search.add(checkboxPanel);
		search.add(RSRP);
		search.add(SINR);
		search.add(RFPot);
		search.add(ExceptTarget);
		search.add(ExceptPot);
		search.add(jbtnPanel);
		return search;
	}
	private JPanel GUI_TOP() {
		JPanel top = new JPanel(new GridLayout(0,3));
		top.setBorder(new EmptyBorder(10,30,-2,30));
		JLabel topLeftLabel = new JLabel("KT");
		topLeftLabel.setFont(new Font("Times", Font.BOLD, 20));
		JLabel topMidLabel = new JLabel("ALPHA WiNG");
		topMidLabel.setFont(new Font("Times", Font.BOLD, 30));
		topMidLabel.setForeground(Color.red);
		JLabel topRightLabel = new JLabel("부산네트워크운용본부", SwingConstants.RIGHT);
		topRightLabel.setFont(new Font("Times", Font.BOLD, 20));
		top.add(topLeftLabel);
		top.add(topMidLabel);
		top.add(topRightLabel);
		return top;
	}
	private FileDialog openFile() {
		Frame f = new Frame();
		FileDialog fileD = new FileDialog(f, "Open", FileDialog.LOAD);
		fileD.setFile("*.xls");
		fileD.setVisible(true);
		return fileD;
	}
	private JTextField newTextfield(int size, int weight, int fontSize) {
		JTextField jtf = new JTextField(size);
		jtf.setEditable(false);
		jtf.setBackground(Color.WHITE);
		jtf.setFont(new Font("Times", weight, fontSize));
		return jtf;
	}
	private JButton newJButton(String str) {
		JButton jt = new JButton(str);
		jt.setFont(new Font("Times", Font.BOLD, 13));
		jt.setBorder(new EtchedBorder());
		return jt;
	}
	private JLabel newLabel(String str, int weight, int size, Color color, int Dx) {
		JLabel jl = new JLabel("  "+str+"  ", JLabel.CENTER);
		jl.setFont(new Font("Times", weight, size));
		jl.setBorder(new BevelBorder(BevelBorder.LOWERED));
		jl.setBackground(color);
		jl.setPreferredSize(new Dimension(Dx, 30));
		return jl;
	}
	public static void main(String[] args) {
		Main T = new Main();
		T.GUI();
		try{
			t1.join();
			jf.dispose();
		}catch(InterruptedException e){ e.printStackTrace(); }
	}
	
	private void Export_Excel(){
		String currentDir = System.getProperty("user.dir");
		SimpleDateFormat df = new SimpleDateFormat("yyMMdd_HHmmss");
		Date date = new Date();
		String FileName = currentDir+"\\ALPHA_WiNG"+"_"+df.format(date)+".csv";
		try {
			CSVWriter cw = new CSVWriter(new OutputStreamWriter(new FileOutputStream(FileName), "EUC-KR"),',', '"');
			String menu[] = {"ID", "장비명", "시/도", "시/군/구", "읍/면/동", "번지", "상세주소", "설치장소",
					 "RSRP AVG", "SINR AVG", "TX_PWR AVG",
					 "측정수 ", "RF_Pot수", "평균측정수"};
//			int SHEET_SIZE[] = {3650,4500,2800,2600,2800,2600,5500,2800,3800,3800,3800,3000,3000,3000};
			cw.writeNext(menu);
			Iterator<String> itr = SearchData.keySet().iterator();
			excelBar.setValue(SearchData.size()-1);
			for(int i=0;i<SearchData.size();i++){
				excelBar.setValue(i);
				String key = itr.next();
				data tmpData = SearchData.get(key);
				if(RSRPmin!=null && !RSRPmin.equals("") && tmpData.RSRP_AVG<Double.parseDouble(RSRPmin)) continue;
				if(RSRPmax!=null && !RSRPmax.equals("") && tmpData.RSRP_AVG>=Double.parseDouble(RSRPmax)) continue;
				if(SINRmin!=null && !SINRmin.equals("") && tmpData.SINR_AVG<Double.parseDouble(SINRmin)) continue;
				if(SINRmax!=null && !SINRmax.equals("") && tmpData.SINR_AVG>=Double.parseDouble(SINRmax)) continue;
				if(RFPotmin!=null && !RFPotmin.equals("") && tmpData.RFPot_SUM<Integer.parseInt(RFPotmin)) continue;
				if(RFPotmax!=null && !RFPotmax.equals("") && tmpData.RFPot_SUM>=Integer.parseInt(RFPotmax)) continue;
				String data[] = {tmpData.target.REPEATER_ID, tmpData.target.REPEATER_NAME, tmpData.target.City,
						tmpData.target.Gu, tmpData.target.Dong,tmpData.target.Street,tmpData.target.Detail,
						tmpData.target.InstallPot,Double.toString(tmpData.RSRP_AVG),Double.toString(tmpData.SINR_AVG),
						Double.toString(tmpData.TXPWR_AVG),Double.toString(tmpData.LTE_SUM),
						Double.toString(tmpData.RFPot_SUM),Double.toString(tmpData.LTE_AVG)};
				cw.writeNext(data);
			}
			cw.close();
		} catch (Exception e) { e.printStackTrace(); }

	}
	private void Search_Data() {
		if(TargetData.size()<1){
			JOptionPane.showMessageDialog(null, "DRM에 의해 대상국소 데이터를 분석하지 못하였습니다.", "Error", JOptionPane.WARNING_MESSAGE);
			System.exit(1);
		}else if(RawData.size()<1){
			JOptionPane.showMessageDialog(null, "DRM에 의해 "+resultSearchTF.getText().trim()+" 데이터를 분석하지 못하였습니다.", "Error", JOptionPane.WARNING_MESSAGE);
			System.exit(1);
		}
//		System.out.println(TargetData.size() +" / " +RawData.size());
		double distance = this.distance;
		searchBar.setMaximum(TargetData.size()-1);
		for(int i=0;i<TargetData.size();i++){
			searchBar.setValue(i);
			double T_Latitude = TargetData.get(i).Latitude, T_longitude = TargetData.get(i).Longitude;
			double RSRP_SUM = 0, SINR_SUM = 0, TXPWR_SUM = 0;
			long RSPR_CNT = 0, SINR_CNT = 0, TXPWR_CNT = 0;
			int LTE_SUM = 0, RFPot_SUM = 0, LTE_CNT = 0, RFPot_CNT = 0;
			LinkedList<raw> tmpRaw = new LinkedList<raw>();
			for(int j=0;j<RawData.size();j++){
				double R_Latitude = RawData.get(j).Latitude, R_Longitude = RawData.get(j).Longitude;
				double tt = Distance_Calc(T_Latitude, T_longitude, R_Latitude, R_Longitude);
				if(tt<=distance){
					raw tmp = RawData.get(j);
					tmpRaw.add(tmp);
					RSRP_SUM += tmp.LTE_RSRP_AVG;
					RSPR_CNT++;
					SINR_SUM += tmp.LTE_SINR_AVG;
					SINR_CNT++;
					TXPWR_SUM += tmp.LTE_TX_PWR_AVG;
					TXPWR_CNT++;
					LTE_SUM += tmp.LTE;
					LTE_CNT++;
					RFPot_SUM += tmp.LTE_PF_Pot;
					RFPot_CNT++;
				}
			}
			SearchData.put(TargetData.get(i).REPEATER_ID
					, new data(tmpRaw, TargetData.get(i), RSRP_SUM, RSPR_CNT, SINR_SUM, SINR_CNT, TXPWR_SUM, TXPWR_CNT, LTE_SUM, LTE_CNT, RFPot_SUM, RFPot_CNT));
			SearchData_NAME.put(TargetData.get(i).REPEATER_NAME, TargetData.get(i).REPEATER_ID);
		}
	}
	private double Distance_Calc(double T_lati, double T_long, double R_lati, double R_long) {
		double d2r = Math.PI/180;
	    double dLon = (R_long - T_long) * d2r;
		double dLat = (R_lati - T_lati) * d2r;
		double a = Math.pow(Math.sin(dLat / 2.0), 2)
		               + Math.cos(T_lati * d2r)
		               * Math.cos(R_lati * d2r)
		               * Math.pow(Math.sin(dLon / 2.0), 2);
		double c = Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)) * 2;
		double distance = c * 6378;
		return distance*1000;
	}
	private void Read_Data_File(int menu){
		/** menu
		 *  0: Target - 대상_RAW.xls, 1: Raw - Outdoor_RAW.xls
		 */
		String file = menu==0?TargetFileName:RawFileName;
		if(file.equals("nullnull") || file.equals("") || file==null){ 
			JOptionPane.showMessageDialog(null, "데이터 오류!", "Error", JOptionPane.WARNING_MESSAGE);
			System.exit(1);
		}
		try {
			FileInputStream fis = new FileInputStream(file);
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheetAt(0);
			int rows = sheet.getPhysicalNumberOfRows();
			int columnindex = 10, rowStart = 1;
			if(menu==0){ targetBar.setMaximum(rows-1); 
			}else{
				columnindex = 11;
				rowStart = 3;
				rawBar.setMaximum(rows++);
			}
			for(int i=rowStart;i<rows;i++){
				XSSFRow row = sheet.getRow(i);
				String v[] = new String[12];
				boolean check = true;
				for(int j=0;j<columnindex;j++){
					XSSFCell data = row.getCell(j);
					v[j] = data==null?"0":data.toString();
					if(v[j]==null || v[j].equals("")) v[j] = "0";
					for(int k=0;k<4;k++)
						if(menu==0 && j==1 && ExceptTargetStr[k] != null && v[1].indexOf(ExceptTargetStr[k])>=0){ check = false; break; }
					if(menu==0 && j==7)
						for(int k=0;k<4;k++)
							if(ExceptPotStr[k] != null && !ExceptPotStr[k].equals("") && v[7].indexOf(ExceptPotStr[k])>=0) { check = false; break; 	}
				}
				if(check && menu==0) 
					TargetData.add(new target(v[0],v[1],v[2],v[3],v[4],v[5],v[6],v[7],v[8],v[9]));
				else if(check && menu==1) 
					RawData.add(new raw(v[0],v[1],v[2],v[3],v[4],v[5],v[6],v[7],v[8],v[9],v[10]));
				if(menu==0) targetBar.setValue(i);
				else rawBar.setValue(i);
			}
			fis.close();
		} catch (IOException e) { e.printStackTrace(); }
	}
	class SearchThread implements Runnable{
		@Override
		public void run() {
			Read_Data_File(0);
			Read_Data_File(1);
			Search_Data();
			if(checkbox.isSelected())
				Export_Excel();
			JOptionPane.showMessageDialog(null, "데이터 분석이 완료되었습니다.");
			jf.dispose();
		}
	}
	class MyThread implements Runnable {
		@Override
		public void run() {
			jf.setLayout(new FlowLayout());
			screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension frameSize = jf.getSize();
			jf.setLocation(((screenSize.width-frameSize.width)/2)-300, ((screenSize.height-frameSize.height)/2)-300);
			JPanel ProgressTargetPanel = new JPanel();
			ProgressTargetPanel.setLayout(new GridLayout(2,1));
			ProgressTargetPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
			ProgressTargetPanel.setPreferredSize(new Dimension(250, 80));
			JLabel targetLabel = new JLabel(" * 대상국소 데이터 분석률");
			targetLabel.setFont(new Font("Times", Font.BOLD, 13));
			targetBar = new JProgressBar();
			targetBar.setStringPainted(true);
			ProgressTargetPanel.add(targetLabel);
			ProgressTargetPanel.add(targetBar);
			
			
			JPanel ProgressRawPanel = new JPanel();
			ProgressRawPanel.setLayout(new GridLayout(2,1));
			ProgressRawPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
			ProgressRawPanel.setPreferredSize(new Dimension(250, 80));
			JLabel rawLabel = new JLabel(" * "+jl2.getText()+"데이터 분석률");
			rawLabel.setFont(new Font("Times", Font.BOLD, 13));
			rawBar = new JProgressBar();
			rawBar.setStringPainted(true);
			ProgressRawPanel.add(rawLabel);
			ProgressRawPanel.add(rawBar);
			
			JPanel ProgressSearchPanel = new JPanel();
			ProgressSearchPanel.setLayout(new GridLayout(2,1));
			ProgressSearchPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
			ProgressSearchPanel.setPreferredSize(new Dimension(250, 80));
			JLabel searchLabel = new JLabel(" * 데이터 비교 분석률");
			searchLabel.setFont(new Font("Times", Font.BOLD, 13));
			searchBar = new JProgressBar();
			searchBar.setStringPainted(true);
			ProgressSearchPanel.add(searchLabel);
			ProgressSearchPanel.add(searchBar);
			JPanel ProgressExcelPanel = null;
			
			if(checkbox.isSelected()){
				ProgressExcelPanel = new JPanel();
				ProgressExcelPanel.setLayout(new GridLayout(2,1));
				ProgressExcelPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
				ProgressExcelPanel.setPreferredSize(new Dimension(250, 80));
				JLabel excelLabel = new JLabel(" * 엑셀 데이터 저장률");
				excelLabel.setFont(new Font("Times", Font.BOLD, 13));
				excelBar = new JProgressBar();
				excelBar.setStringPainted(true);
				ProgressExcelPanel.add(excelLabel);
				ProgressExcelPanel.add(excelBar);
			}
			
			jf.add(ProgressTargetPanel);
			jf.add(ProgressRawPanel);
			jf.add(ProgressSearchPanel);
			if(checkbox.isSelected())
				jf.add(ProgressExcelPanel);
			jf.setSize(300, checkbox.isSelected()?400:300);
			jf.setResizable(false);
			jf.setVisible(true);
		}
	}
}
