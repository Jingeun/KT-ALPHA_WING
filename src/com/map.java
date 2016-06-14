package com;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class map extends JFrame{
	private final JProgressBar progressBar = new JProgressBar();
	private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;
    private final JLabel lblStatus = new JLabel();
    private final JPanel panel = new JPanel(new BorderLayout());
    private String url = null;
    private JComboBox Zoom, mapType;
    private String[] zoomNum, mapTypeString, mapString;
    private target t;
    private LinkedList<raw> r;
    private Dimension screenSize;
	public map(){
		super("지도");
		zoomNum = new String[21];
        for(int i=1;i<=21;i++)
        	zoomNum[i-1] = String.valueOf(i);
        mapTypeString = new String[4];
        mapTypeString[0] = "로드맵";
        mapTypeString[1] = "위성";
        mapTypeString[2] = "지형";
        mapTypeString[3] = "하이브리드";
        mapString = new String[4];
        mapString[0] = "roadmap";
        mapString[1] = "satellite";
        mapString[2] = "terrain";
        mapString[3] = "hybrid";
		initComponents();
	}

	public void loadURL(Dimension screenSize, target t, LinkedList<raw> r) {
		this.t = t;
		this.r = r;
		this.screenSize = screenSize;
		url = "https://maps.googleapis.com/maps/api/staticmap?center=";
		url += t.Latitude+","+t.Longitude+"&zoom="+zoomNum[Zoom.getSelectedIndex()];
		url += "&size="+(screenSize.width/3)+"x"+((screenSize.height*3/4)-95);
		url += "&maptype="+mapString[mapType.getSelectedIndex()]+"&markers=color:red%7Clabel:T%7C"+t.Latitude+","+t.Longitude;
		for(int i=0;i<r.size();i++)
			url += "&markers=size:mid%7Ccolor:blue%7C"+r.get(i).Latitude+","+r.get(i).Longitude;
		url += "&key=AIzaSyDPq2c2yamOy9IzYFhnb4T2oKyeOnaeZxQ";
        runable();
    }
	private void runable() {
		Platform.runLater(new Runnable() { 
			public void run() { engine.load(toURL(url)); 
			} 
		});
	}
    private static String toURL(String str) {
        try { return new URL(str).toExternalForm(); } 
        catch (MalformedURLException exception) { return null; }
    }
	private void initComponents() {
        createScene();
  
        progressBar.setPreferredSize(new Dimension(150, 18));
        progressBar.setStringPainted(true);
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        Zoom = new JComboBox(zoomNum);
        Zoom.setSelectedIndex(17);
        JLabel zoomLabel = new JLabel("Zoom");
        JLabel mapTypeLabel = new JLabel("        Type");
        mapType = new JComboBox(mapTypeString);
        mapType.setSelectedIndex(0);
        JButton mapButton = new JButton("검색");
        mapButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				url = "https://maps.googleapis.com/maps/api/staticmap?center=";
				url += t.Latitude+","+t.Longitude+"&zoom="+zoomNum[Zoom.getSelectedIndex()];
				url += "&size="+(screenSize.width/3)+"x"+((screenSize.height*3/4)-95);
				url += "&maptype="+mapString[mapType.getSelectedIndex()]+"&markers=color:red%7Clabel:T%7C"+t.Latitude+","+t.Longitude;
				for(int i=0;i<r.size();i++)
					url += "&markers=color:blue%7C"+r.get(i).Latitude+","+r.get(i).Longitude;
				url += "&key=AIzaSyDPq2c2yamOy9IzYFhnb4T2oKyeOnaeZxQ";
				runable();
			}
		});
        topBar.add(zoomLabel);
        topBar.add(Zoom);
        topBar.add(mapTypeLabel);
        topBar.add(mapType);
        topBar.add(mapButton);
        
        JPanel statusBar = new JPanel(new BorderLayout(5, 0));
        statusBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        statusBar.add(lblStatus, BorderLayout.CENTER);
        statusBar.add(progressBar, BorderLayout.EAST);
 
        panel.add(topBar, BorderLayout.NORTH);
        panel.add(jfxPanel, BorderLayout.CENTER);
        panel.add(statusBar, BorderLayout.SOUTH);
        
        getContentPane().add(panel);
        pack();
    }
	private void createScene() {
        Platform.runLater(new Runnable() {
            @Override 
            public void run() {
                WebView view = new WebView();
                engine = view.getEngine();
                engine.getLoadWorker().workDoneProperty().addListener(new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, final Number newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() { 
                            	progressBar.setValue(newValue.intValue());
                            	if(newValue.intValue()==100){
                            		progressBar.setValue(newValue.intValue());
                            		try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace();}
                            		progressBar.setVisible(false);
                            	}
                            }
                        });
                    }
                });
                jfxPanel.setScene(new Scene(view));
            }
        });
    }
	public void loadClickEvent(int selectedRow) {
		url = "https://maps.googleapis.com/maps/api/staticmap?center=";
		url += t.Latitude+","+t.Longitude+"&zoom="+zoomNum[Zoom.getSelectedIndex()];
		url += "&size="+(screenSize.width/3)+"x"+((screenSize.height*3/4)-95);
		url += "&maptype="+mapString[mapType.getSelectedIndex()]+"&markers=color:red%7Clabel:T%7C"+t.Latitude+","+t.Longitude;
		for(int i=0;i<r.size();i++){
			url += "&markers=";
			if(selectedRow!=i) url+="size:small%7Ccolor:blue%7C";
			else url += "color:yellow%7Clabel:R%7C";
			url += r.get(i).Latitude+","+r.get(i).Longitude;
		}
		url += "&key=AIzaSyDPq2c2yamOy9IzYFhnb4T2oKyeOnaeZxQ";
		runable();
	}
}
