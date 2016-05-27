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
    private plane[] Flota;
    
    class Dzialanie extends TimerTask{
        public void run(){
            for(int i = 0; i < 6; i++){
                if(Flota[i].getLane()){
                    if(Flota[i].getY() > 400){
                        Flota[i].setY(Flota[i].getY() - 1);                        
                    }
                }else{
                    if(Flota[i].getX() > 350){
                        Flota[i].setX(Flota[i].getX() - 1);                        
                    }
                } 
            }

            

            repaint();
        }
    }
    
    public class plane {
        private final int wsp[] = new int[2];
        private boolean tor;
        
        public plane(int x, int y, boolean lane){
            wsp[0] = x;
            wsp[1] = y;
            tor = lane;
        }
        
        public int getX(){
            return wsp[0];
        }
        
        public int getY(){
            return wsp[1];
        }
        
        public boolean getLane(){
            return tor;
        }
        
        public void setX(int x){
            wsp[0] = x;
        }
        
        public void setY(int y){
            wsp[1] = y;
        }
        
        public void changeLane( boolean lane){
            tor = lane;
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

        Flota = new plane[6];
        for(int i = 0; i < 6; i++){            
            Flota[i] = new plane(950, 955, Math.random() < 0.5 );
            System.out.println("Samolot nr" + i + " [" + Flota[i].getX() + "," + Flota[i].getY() + "] " + Flota[i].getLane());
        }

        zegar = new Timer();
        zegar.scheduleAtFixedRate(new Dzialanie(), 0, 20);   
    }
    
    public static void main(String[] args) {
        AirPort window = new AirPort();
        window.repaint();  
    }
    
    @Override
    public void paint(Graphics g){
        BufferStrategy bstrategy = this.getBufferStrategy();
        Graphics2D g2D = (Graphics2D)bstrategy.getDrawGraphics();
        
        g2D.drawImage(lotnisko, 0,  26, null);   // Nie wiem dlaczego ale obcina 26px z góry.
        for(int i = 0; i < 6; i++ ){
            g2D.drawImage(samolot,  Flota[i].getX(),  Flota[i].getY() + 26, null);    // Trzeba zawsze dodać 26 do wsp y.
        }
        
        
        bstrategy.show();
    }
}
