
import java.awt.*;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author Aleksandra Wyszecka
 * @author Damian Winczewski
 */
public class AirPort extends JFrame{
    
    private Image lotnisko;
    private Image samolot;    
    private Timer zegar;

    
    class Dzialanie extends TimerTask{
        public void run(){
            
            repaint();
        }
    }
    
    AirPort(){
        //Okno
       super("Kontroler Lotów");
       setBounds(10, 10, 1000, 1026);
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       setResizable(false);
       setVisible(true);
       createBufferStrategy(2);
       
       lotnisko = new ImageIcon("Obrazki/lotnisko.png").getImage();
       samolot = new ImageIcon("Obrazki/samolot.png").getImage();      
       
       zegar = new Timer();
       zegar.scheduleAtFixedRate(new Dzialanie(), 0, 20);
       

       
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AirPort window = new AirPort();
        window.repaint();
    }
    
    public void paint(Graphics g){
        BufferStrategy bstrategy = this.getBufferStrategy();
        Graphics2D g2D = (Graphics2D)bstrategy.getDrawGraphics();
        
        g2D.drawImage(lotnisko, 0,  26, null);   // Nie wiem dlaczego ale obcina 26px z góry.
        g2D.drawImage(samolot,  0,  26, null);    // Trzeba zawsze dodać 26 do wsp y.
        
        bstrategy.show();
    }
}
