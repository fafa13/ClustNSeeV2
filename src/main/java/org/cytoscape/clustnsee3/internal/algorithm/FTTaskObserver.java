/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 20 avr. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.algorithm;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.util.Iterator;

import javax.swing.ImageIcon;

import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskObserver;

/**
 * 
 */
public class FTTaskObserver implements TaskObserver {
	private CyNetworkView view;
	private CnSCluster cluster;
	private RenderingEngine<?> re = null;
	
	/**
	 * @param
	 * @return
	 */
	public FTTaskObserver(CyNetworkView v, CnSCluster c) {
		super();
		cluster = c;
		view = v;
		CnSEvent ev = new CnSEvent(CyActivator.GET_RENDERING_ENGINE_MANAGER, CnSEventManager.CY_ACTIVATOR);
		RenderingEngineManager rem = (RenderingEngineManager)CnSEventManager.handleMessage(ev);
	
		Iterator<RenderingEngine<?>> it = rem.getAllRenderingEngines().iterator();
		while (it.hasNext()) {
			re = it.next();
			if (re.getViewModel() == view) break;
		}
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.work.TaskObserver#allFinished(org.cytoscape.work.FinishStatus)
	 */
	@Override
	public void allFinished(FinishStatus arg0) {
		//view.fitContent();
		//view.updateView();
		///re.createImage(200,  100);
		//BufferedImage image = new BufferedImage( 200, 100, BufferedImage.TYPE_INT_RGB);
        //final Graphics2D g = (Graphics2D) image.getGraphics();
		//if (re != null) re.printCanvas(g);
		BufferedImage i = (BufferedImage)re.createImage(200, 112);
		//JOptionPane.showConfirmDialog(null, i.getClass());
		ImageFilter filter = new RGBImageFilter() {
	         int transparentColor = Color.white.getRGB() | 0xFF000000;

	         public final int filterRGB(int x, int y, int rgb) {
	            if ((rgb | 0xFF000000) == transparentColor) {
	               return 0x00FFFFFF & rgb;
	            } else {
	               return rgb;
	            }
	         }
	      };
		ImageProducer filteredImgProd = new FilteredImageSource(i.getSource(), filter);
	    Image transparentImg = Toolkit.getDefaultToolkit().createImage(filteredImgProd);

		cluster.setSnapshot(new ImageIcon(transparentImg));
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.work.TaskObserver#taskFinished(org.cytoscape.work.ObservableTask)
	 */
	@Override
	public void taskFinished(ObservableTask arg0) {
		// TODO Auto-generated method stub
		
	}

}
