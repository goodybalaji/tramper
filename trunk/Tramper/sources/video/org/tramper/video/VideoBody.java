package org.tramper.video;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import org.tramper.doc.Library;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.doc.Video;
import org.tramper.gui.viewer.Body;

import com.sun.media.jmc.MediaProvider;
import com.sun.media.jmc.control.VideoRenderControl;
import com.sun.media.jmc.event.VideoRendererEvent;
import com.sun.media.jmc.event.VideoRendererListener;

/**
 * 
 * @author Paul-Emile
 */
public class VideoBody extends JPanel implements Body, VideoRendererListener, MouseListener {
    /** VideoBody.java long */
    private static final long serialVersionUID = 1L;
    /**  */
    private VideoRenderControl videoRenderCtrl = null;
    /** target */
    private Target target;
    /** video */
    private Video document;
    
    public VideoBody() {
	super();
	this.setBackground(Color.BLACK);
	this.setOpaque(true);
	this.addMouseListener(this);
    }
    
    /**
     * 
     * @see org.tramper.gui.viewer.Body#displayDocument(org.tramper.doc.SimpleDocument, org.tramper.doc.Target, int)
     */
    public void displayDocument(SimpleDocument doc, Target target, int documentPart) {
	if (!(doc instanceof Video)) {
	    throw new RuntimeException(doc.getTitle()+" is not a video");
	}
	this.target = target;
	document = (Video)doc;
	MediaProvider mediaProvider = document.getMediaProvider();
	videoRenderCtrl = mediaProvider.getControl(VideoRenderControl.class);
	videoRenderCtrl.addVideoRendererListener(this);
    }
    
    public void first() {
    }
    
    public void last() {
    }
    
    public void next() {
    }
    
    public void previous() {
    }
    
    /**
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     */
    @Override
    public void paint(Graphics g) {
	//super.paint(g);
	Graphics2D g2d = (Graphics2D)g;
	
	int videoWidth = videoRenderCtrl.getFrameSize().width;
	int videoHeight = videoRenderCtrl.getFrameSize().height;
	int bodyWidth = getWidth();
	int bodyHeight = getHeight();

	// the background of this panel is not black with Substance look and feel,
	// so we paint it black ourselves
	g2d.setColor(getBackground());
	g2d.fillRect(0, 0, bodyWidth, bodyHeight);
	
	// paint the video in normal size centered in the panel
	Rectangle normalVideoSize = new Rectangle(0, 0, videoWidth, videoHeight);
	g2d.translate((bodyWidth - videoWidth)/2, (bodyHeight - videoHeight)/2);
	videoRenderCtrl.paintVideo(g2d, normalVideoSize, normalVideoSize);
	//videoRenderCtrl.paintVideoFrame(g2d, normalVideoSize);

	g2d.translate(0, videoHeight);
	
	// paint the reflected reverted half video
	g2d.setComposite(AlphaComposite.SrcOver.derive(0.5f));
	Rectangle halfVideoSize = new Rectangle(0, videoHeight/2, videoWidth, videoHeight/2);
	AffineTransform currentTransform = g2d.getTransform();
	AffineTransform flipTransform = new AffineTransform(1, 0, 0, -1, 0, videoHeight);
	g2d.transform(flipTransform);
	videoRenderCtrl.paintVideo(g2d, halfVideoSize, halfVideoSize);
	g2d.setTransform(currentTransform);
	
	// paint the transparent layer for reflected half video
	g2d.setComposite(AlphaComposite.SrcOver.derive(1.0f));
	GradientPaint gradientRightIn = new GradientPaint(0, 0, new Color(0, 0, 0, 0), 0, videoHeight/2f, getBackground());
	g2d.setPaint(gradientRightIn);
	Rectangle2D.Float transparentLayer = new Rectangle2D.Float(0, 0, videoWidth, videoHeight/2f);
	g2d.fill(transparentLayer);
	
	g2d.translate(0, -videoHeight);
	
	g2d.translate(-(bodyWidth - videoWidth)/2, -(bodyHeight - videoHeight)/2);
    }
    
    /**
     * 
     * @see com.sun.media.jmc.event.VideoRendererListener#videoFrameUpdated(com.sun.media.jmc.event.VideoRendererEvent)
     */
    public void videoFrameUpdated(VideoRendererEvent arg0) {
	this.repaint();
    }

    public void mouseClicked(MouseEvent arg0) {
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void mousePressed(MouseEvent event) {
	int clickedButton = event.getButton();
	if (clickedButton == MouseEvent.BUTTON1) {
	    Library.getInstance().setActiveDocument(target);
	}
    }

    public void mouseReleased(MouseEvent arg0) {
    }

    /**
     * @see org.tramper.gui.viewer.Body#paintMiniature(java.awt.Graphics2D, java.awt.Dimension, boolean)
     */
    public void paintMiniature(Graphics2D g2d, Dimension miniatureSize, boolean mouseOver) {
	int videoWidth = videoRenderCtrl.getFrameSize().width;
	int videoHeight = videoRenderCtrl.getFrameSize().height;

	double scale = (double)miniatureSize.width/(double)videoWidth;
	if (scale > 1) {
	    scale = 1.0;
	}
	g2d.scale(scale, scale);
	int x = (miniatureSize.width - videoWidth)/2;
	if (x < 0) {
	    x = 0;
	}
	int y = (miniatureSize.height - videoHeight)/2;
	if (y < 0) {
	    y = 0;
	}
	Rectangle normalVideoSize = new Rectangle(0, 0, videoWidth, videoHeight);
	videoRenderCtrl.paintVideo(g2d, normalVideoSize, normalVideoSize);
	
	// reset scale
	g2d.scale(1/scale, 1/scale);
    }
}
