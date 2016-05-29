import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
/**
 * @author Aleksandra Wyszecka
 * @author Damian Winczewski
 */
public class AirPort extends JFrame{
    
    public static void main(String[] args) {
        AirPort window = new AirPort();
        window.repaint();  
    }
    
    private Image menu;
    private Image lotnisko;
    private Image samolot;
    
    private boolean start = true; // zmienić na false   //wprowadzić zmiane
    private int level = 1; 
    private final plane[] Flota;
        
    private Timer zegar;
    

    
    class Dzialanie extends TimerTask{
        public void run(){
            if(start){
                for(int i = 0; i < 6; i++){
                    if(level <= 1){
                        Flota[i].setSpeed(1);
                    }else if(level == 2){
                        Flota[i].setSpeed(2);
                    }else if(level >= 3){      //poziom nie do przejścia
                        Flota[i].setSpeed(4);   //prawdopodobnie trzba mocno zwiększyć prędkość
                    }                           //sprawdzić drogi czy nie wypada z wyjątków
                    
                    if(Flota[i].getLane()){
                        if(Flota[i].getY() > 500 && Flota[i].getX() >= 950){
                            Flota[i].setY(Flota[i].getY() - Flota[i].getSpeed());
                            
                        }else if(Flota[i].getY() < 501 && Flota[i].getY() > 390 
                                && Flota[i].getX() <= 950 && Flota[i].getX() > 840){
                            //trzeba zrobić ruch po okregu
                            Flota[i].setY(Flota[i].getY() - Flota[i].getSpeed());  
                            Flota[i].setX(Flota[i].getX() - Flota[i].getSpeed());
                        }
                    }else{
                        if(Flota[i].getX() > 500 && Flota[i].getY() >= 955){
                            Flota[i].setX(Flota[i].getX() - Flota[i].getSpeed());                        
                        }
                    } 
                }
            }else{
                //Nic nie robie jeśli nie włączono gry
            }
        repaint();
        }
    }
    
    public class plane{
        private final int   wsp[] = new int[2];
        private double      degree; //kąt skretu        
        private boolean     lane;
        private boolean     position;
        private int         speed;
        
        public plane(int x, int y, boolean iLane){
            wsp[0] =    x;
            wsp[1] =    y;
            lane =      iLane;
            position =  true; //w powietrzu
            degree =    0;
        }
        
        //Sprawdzanie     
            public int      getX(){
                return wsp[0];
            }        
            public int      getY(){
                return wsp[1];
            }       
            public boolean  getLane(){
                return lane;
            }        
            public boolean  getPosition(){
                return position;
            }
            public int      getSpeed(){
                return speed;
            }
            public double   getDegree(){
                return degree;
            }
        //Ustawianie
            public void     setX(int iX){
                wsp[0] = iX;
            }        
            public void     setY(int iY){
                wsp[1] = iY;
            }        
            public void     changeLane( boolean iLane){
                lane = iLane;
            }        
            public void     changePosition( boolean iPosition){
                position = iPosition;
            }
            public void     setSpeed(int iSpeed){
                speed = iSpeed;
            }
            public void     setDegree(double iDegree){
                degree = iDegree;
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

        menu = new ImageIcon("Obrazki/menu.png").getImage();
        lotnisko = new ImageIcon("Obrazki/lotnisko.png").getImage();
        samolot = new ImageIcon("Obrazki/samolot.png").getImage();      

        Flota = new plane[6];
        for(int i = 0; i < 6; i++){
            boolean iLane = Math.random() < 0.5;
            if(iLane){
                Flota[i] = new plane(950, 955 + i * 50 * level, iLane);
            }else{
                Flota[i] = new plane(950 + i * 50 * level, 955, iLane);
            }
        System.out.println("Samolot nr" + i + " [" + Flota[i].getX() + "," + Flota[i].getY() + "] " + Flota[i].getLane());    
        }
        
        
        zegar = new Timer();
        zegar.scheduleAtFixedRate(new Dzialanie(), 0, 20);   
    }
     
    @Override
    public void paint(Graphics g){
        BufferStrategy bstrategy = this.getBufferStrategy();
        Graphics2D g2D = (Graphics2D) bstrategy.getDrawGraphics();
        
        if(start){
            //Jeśli zaczęto grę rysowanie lotniska
            g2D.drawImage(lotnisko, 0,  26, null);   // Nie wiem dlaczego ale obcina 26px z góry.
            for(int i = 0; i < 6; i++ ){
                g2D.drawImage(samolot,  Flota[i].getX(),  Flota[i].getY() + 26, null);    // Trzeba zawsze dodać 26 do wsp y.
            }
        }else{
            //Rysowanie menu
            g2D.drawImage(menu, 0,  26, null);      // Nie wiem dlaczego ale obcina 26px z góry.
        }
        
        g2D.dispose();
        bstrategy.show();
    }
}
