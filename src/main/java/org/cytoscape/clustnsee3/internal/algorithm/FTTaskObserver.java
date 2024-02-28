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
	private ImageFilter filter = new RGBImageFilter() {
	         int transparentColor = Color.white.getRGB() | 0xFF000000;

	         public final int filterRGB(int x, int y, int rgb) {
	            if ((rgb | 0xFF000000) == transparentColor) {
	               return 0x00FFFFFF & rgb;
	            } else {
	               return rgb;
	            }
	         }
	      };
		
	/**
	 * @param
	 * @return
	 */
	public FTTaskObserver(CyNetworkView v, CnSCluster c) {
		super();
		cluster = c;
		view = v;
		
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.work.TaskObserver#allFinished(org.cytoscape.work.FinishStatus)
	 */
	@Override
	public void allFinished(FinishStatus arg0) {
		CnSEvent ev = new CnSEvent(CyActivator.GET_RENDERING_ENGINE_MANAGER, CnSEventManager.CY_ACTIVATOR, this.getClass());
		RenderingEngineManager rem = (RenderingEngineManager)CnSEventManager.handleMessage(ev, true).getValue();
	
		System.err.println("--- Cluster : " + cluster.getID());
			
		Iterator<RenderingEngine<?>> it = rem.getRenderingEngines(view).iterator();
		while (it.hasNext()) {
			re = it.next();
			if (re.getViewModel() == view) break; else re = null;
		}
		while (re == null) {
			it = rem.getRenderingEngines(view).iterator();
			while (it.hasNext()) {
				re = it.next();
				if (re.getViewModel() == view) break; else re = null;
			}
		}
		System.err.println("--- re = " + re);
		if (re != null) {
			BufferedImage i = (BufferedImage)re.createImage(200, 100);
			System.err.println("--- i = " + i);
			ImageProducer filteredImgProd = new FilteredImageSource(i.getSource(), filter);
			Image transparentImg = Toolkit.getDefaultToolkit().createImage(filteredImgProd);

			cluster.setSnapshot(new ImageIcon(transparentImg));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.work.TaskObserver#taskFinished(org.cytoscape.work.ObservableTask)
	 */
	@Override
	public void taskFinished(ObservableTask arg0) {
		// TODO Auto-generated method stub
		
	}

}
