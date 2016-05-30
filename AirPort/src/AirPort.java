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
    
    private boolean start       = true;   // zmienić na false   //wprowadzić zmiane
    private int     level       = 1;      //wprowadzić zmiane
    private final   plane[] Flota;
    private double  radDegree   = 0;
    private int     radius      = 100;
        
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
                        if(Flota[i].getX() >= 800 && Flota[i].getY() >= 650){
                            Flota[i].setY(Flota[i].getY() - Flota[i].getSpeed());
                            
                        }else if(Flota[i].getX() > 800 && Flota[i].getY() < 650 && Flota[i].getY() > 500 && Flota[i].getDegree() <= 90 ){
                            Flota[i].setDegree(Flota[i].getDegree() + (1 * Flota[i].getSpeed()));
                            radDegree = (Math.PI * Flota[i].getDegree())/180;
                            //  NIE MAM NA TO SIŁY
                            Flota[i].setX((int) (901d - radius * Math.sin(radDegree)));
                            Flota[i].setY((int) (749d - radius * Math.cos(radDegree)));
                        }
                    }else{  //drugi pas
                        if(Flota[i].getX() > 650 && Flota[i].getY() >= 826){
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
        private boolean     collision;
        
        public plane(int x, int y, boolean iLane){
            wsp[0] =    x;
            wsp[1] =    y;
            lane =      iLane;
            position =  true;   //w powietrzu
            degree =    0;      //do zmian pasa i ladowania //zalezne od lane - do zrobienia
            collision = false;  //czy zderzenie
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
            public boolean  getCollision(){
                return collision;
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
            public void     setCollision( boolean iCollision){
                collision = iCollision;
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
