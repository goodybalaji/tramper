package org.tramper.gui.viewer;

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

import javax.swing.JPanel;

import org.tramper.doc.Library;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.doc.Video;

import com.sun.media.jmc.MediaProvider;
import com.sun.media.jmc.control.VideoRenderControl;
import com.sun.media.jmc.event.VideoRendererEvent;
import com.sun.media.jmc.event.VideoRendererListener;

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
	//this.putClientProperty(SubstanceLookAndFeel.WATERMARK_VISIBLE, Boolean.FALSE);
	//SubstanceLookAndFeel.setDecorationType(this, DecorationAreaType.NONE);
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
	
	Dimension frameSize = videoRenderCtrl.getFrameSize();
	int videoWidth = frameSize.width;
	int videoHeight = frameSize.height;
	int bodyWidth = getWidth();
	int bodyHeight = getHeight();

	// the background of this panel is not black with Substance look and feel,
	// so we paint it black ourselves
	g2d.setColor(getBackground());
	g2d.fillRect(0, 0, bodyWidth, bodyHeight);
	
	Rectangle normalVideoSize = new Rectangle(0, 0, videoWidth, videoHeight);
	
	g2d.translate((bodyWidth - videoWidth)/2, (bodyHeight - videoHeight)/2);

	videoRenderCtrl.paintVideo(g2d, normalVideoSize, normalVideoSize);
	//videoRenderCtrl.paintVideoFrame(g2d, rect);
	
	/*Color highlightColor = UIManager.getColor("textHighlight");
	
	GradientPaint gradientLeftOut = new GradientPaint(-10, 0, new Color(0, 0, 0, 0), 0, 0, highlightColor);
	g2d.setPaint(gradientLeftOut);
	g2d.fillRect(-10, 0, 10, frameSize.height);
	
	GradientPaint gradientRightOut = new GradientPaint(frameSize.width, 0, highlightColor, frameSize.width + 10, 0, new Color(0, 0, 0, 0));
	g2d.setPaint(gradientRightOut);
	g2d.fillRect(frameSize.width, 0, 10, frameSize.height);*/


	g2d.translate(0, videoHeight);

	Rectangle halfVideoSize = new Rectangle(0, videoHeight/2, videoWidth, videoHeight/2);
	
	g2d.setComposite(AlphaComposite.SrcOver.derive(0.5f));
	AffineTransform currentTransform = g2d.getTransform();
	AffineTransform flipTransform = new AffineTransform(1, 0, 0, -1, 0, videoHeight);
	g2d.transform(flipTransform);
	//g2d.rotate(Math.PI, videoWidth/2, videoHeight/2);
	//g2d.scale(0.75, 0.75);
	videoRenderCtrl.paintVideo(g2d, halfVideoSize, halfVideoSize);
	//g2d.scale(1/0.75, 1/0.75);
	//g2d.rotate(-Math.PI, videoWidth/2, videoHeight/2);
	g2d.setTransform(currentTransform);
	g2d.setComposite(AlphaComposite.SrcOver.derive(1.0f));

	/*GradientPaint gradientLeftIn = new GradientPaint(0, 0, Color.BLACK, 10, 0, new Color(0, 0, 0, 0));
	g2d.setPaint(gradientLeftIn);
	g2d.fillRect(0, 0, 10, frameSize.height);
	
	GradientPaint gradientRightIn = new GradientPaint(frameSize.width - 10, 0, new Color(0, 0, 0, 0), frameSize.width, 0, Color.BLACK);
	g2d.setPaint(gradientRightIn);
	g2d.fillRect(frameSize.width - 10, 0, 10, frameSize.height);*/

	GradientPaint gradientRightIn = new GradientPaint(0, 0, new Color(0, 0, 0, 0), 0, videoHeight/2, this.getBackground());
	g2d.setPaint(gradientRightIn);
	g2d.fillRect(0, 0, videoWidth, videoHeight/2);

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
	int documentWidth = this.getWidth();
	int documentHeight = this.getHeight();
	int videoWidth = videoRenderCtrl.getFrameSize().width;

	double scale = (double)miniatureSize.width/(double)videoWidth;
	if (scale > 1) {
	    scale = 1.0;
	}
	g2d.scale(scale, scale);
	g2d.translate(-(documentWidth - miniatureSize.width)/2*scale, -(documentHeight - miniatureSize.height)/2*scale);
	
	this.paint(g2d);

	// reset scale, translation
	g2d.translate((documentWidth - miniatureSize.width)/2*scale, (documentHeight - miniatureSize.height)/2*scale);
	g2d.scale(1/scale, 1/scale);
    }
}
