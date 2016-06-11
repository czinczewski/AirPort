import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.util.Random;
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
    private Timer zegar;
    private boolean start       = false;    //zmienić na false   //wprowadzić zmiane
    private int     level       =   1;      //wprowadzić zmiane
    private final   plane[] Flota;
    private int horizontalParking[];        //parking poziomy
    private int verticalParking[];          //parking pionowy
    private boolean naparkingu = true;
    private boolean parking = true;
    private double  radDegree   =   0;
    private int     radius      = 100;
    private int     radius2     =  50;
    
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
                    
                    if(Flota[i].getLane()){ //wylot pionowy 
                        if(!Flota[i].ifChange()){ //bez zmiany pasa
                            if(Flota[i].getX() >= 600 && Flota[i].getY() >= 460){
                                Flota[i].setY(Flota[i].getY() - Flota[i].getSpeed());
                            }else if(Flota[i].getX() >= 600 && Flota[i].getY() <= 460 && Flota[i].getY() >= 360 && Flota[i].getDegree() < 90 ){
                                Flota[i].setDegree(Flota[i].getDegree() + (Flota[i].getSpeed()));
                                radDegree = (Math.PI * Flota[i].getDegree())/180;
                                Flota[i].setX((int) (600d + radius * Math.cos(radDegree)));
                                Flota[i].setY((int) (460d - radius * Math.sin(radDegree)));
                            }else if(Flota[i].getX() <= 600 && Flota[i].getX() > 115 && Flota[i].getY() <= 360 ){
                                Flota[i].changePosition(false); //wylondowałem
                                Flota[i].setX(Flota[i].getX() - Flota[i].getSpeed());
                            }else if(Flota[i].getX() <= 115 && Flota[i].getY() <=360 && Flota[i].getY() >= 300 && Flota[i].getDegree() <= 180){
                                Flota[i].setDegree(Flota[i].getDegree() + Flota[i].getSpeed());
                                if(Flota[i].getDegree() >= 180){
                                    Flota[i].setDegree(180d);
                                    Flota[i].setY(Flota[i].getY() - Flota[i].getSpeed());                                    
                                }
                            }else if(Flota[i].getY() <= 300 && Flota[i].getY() >= 200){
//                                for(int j = 0; j < 3; j++){
//                                    if( horizontalParking[j] != i){
//                                        parking = false;
//                                    }
//                                }
//                                naparkingu = parking;
//                                if(!naparkingu){
//                                    for(int j = 0; j < 2; j++){
//                                        if( horizontalParking[j] == 10){ //10 bo nigdy nie będzie takiego samolotu
//                                            naparkingu = true; 
//                                            horizontalParking[j] = i;
//                                            System.out.println("Samolot nr " + horizontalParking[j] + " wybrał miejsce " + j + " na parkingu.");
//                                            j=2;
//
//                                        }
//                                    }
//                                }                                
//                                if( i == horizontalParking[0]){
////                                    if(){
////                                        Flota[i].setX(Flota[i].getX() - Flota[i].getSpeed());                                        
////                                    }
////                                    if(){
////                                        Flota[i].setY(Flota[i].getY() - Flota[i].getSpeed());                                        
////                                    }
//                                }else if( i == horizontalParking[1]){
//                                    
//                                }else if( i == horizontalParking[2]){
//                                    
//                                }

                            }
                        } else { //Po zmianie kliknieciu pasa
                            //dodać if za nisko na ekranie - leć jeszcze prosto
                            if(Flota[i].getX() > 600){ //dodać warunek na pozycje Y
                                Flota[i].setDegree(Flota[i].getDegree() + (Flota[i].getSpeed()));
                                radDegree = (Math.PI * Flota[i].getDegree())/180;
                                Flota[i].setX((int) (650d + radius2 * Math.cos(radDegree)));
                                Flota[i].setY((int) (Flota[i].getyChanege() - radius2 * Math.sin(radDegree)));
                            }else if(Flota[i].getX() == 600 && Flota[i].getY() <= 650){
                                if(Flota[i].getX() == 600 ){
                                    Flota[i].setDegree(0d);
                                }
                                Flota[i].setY(Flota[i].getY() + Flota[i].getSpeed());
                            }else if(Flota[i].getY() >= 650 && Flota[i].getX() <= 600 && Flota[i].getX() > 500 && Flota[i].getDegree() < 90){
                                Flota[i].setDegree(Flota[i].getDegree() + (Flota[i].getSpeed()));
                                radDegree = (Math.PI * Flota[i].getDegree())/180;
                                Flota[i].setX((int) (550d + radius2 * Math.cos(radDegree)));
                                Flota[i].setY((int) (650d + radius2 * Math.sin(radDegree)));
                            }else if(Flota[i].getY() >= 650 && Flota[i].getDegree() == 90){
                                Flota[i].setY(700);
                                Flota[i].setX(550);
                                Flota[i].setlaneChange(false);
                                Flota[i].setLane(false);
                                Flota[i].setDegree(0);
                                //System.out.println("Samolot nr" + i + " [" + Flota[i].getX() + ", " + Flota[i].getY() + "] Lane: " + Flota[i].getLane() + " ChLane: " + Flota[i].getlaneChange());
                            }
                        }
                    }else{  //wylot poziomy
                        if(Flota[i].ifChange() == false){ //be zmiany pasa
                            if(Flota[i].getX() >= 460 && Flota[i].getY() >= 600){
                                Flota[i].setX(Flota[i].getX() - Flota[i].getSpeed());                                
                            }else if(Flota[i].getY() >= 600 && Flota[i].getX() <= 460 && Flota[i].getX() >= 360 && Flota[i].getDegree() <= 90){
                                Flota[i].setDegree(Flota[i].getDegree() + (Flota[i].getSpeed()));
                                radDegree = (Math.PI * Flota[i].getDegree())/180;
                                Flota[i].setX((int) (460d - radius * Math.sin(radDegree)));
                                Flota[i].setY((int) (600d + radius * Math.cos(radDegree)));
                            }else if(Flota[i].getY() < 600 && Flota[i].getY() > 115 && Flota[i].getX() <= 360 ){
                                Flota[i].setY(Flota[i].getY() - Flota[i].getSpeed());
                            }else if(Flota[i].getY() <= 115 && Flota[i].getX() <= 360 && Flota[i].getX() >= 300 && Flota[i].getDegree() <= 180){
                                Flota[i].setDegree(Flota[i].getDegree() + Flota[i].getSpeed());
                                if(Flota[i].getDegree() >= 180){
                                    Flota[i].setDegree(180d);
                                    Flota[i].setX(Flota[i].getX() - Flota[i].getSpeed());                                    
                                }
                            }
                        }else{ //zmiana pasa
                            //dodać if za nisko na ekranie - leć jeszcze prosto
                            if(Flota[i].getY() > 600){ 
                                Flota[i].setDegree(Flota[i].getDegree() + (Flota[i].getSpeed()));
                                radDegree = (Math.PI * Flota[i].getDegree())/180;
                                Flota[i].setX((int) (Flota[i].getxChanege() - radius2 * Math.sin(radDegree)));
                                Flota[i].setY((int) (650d + radius2 * Math.cos(radDegree))); 
                            }else if(Flota[i].getY() == 600 && Flota[i].getX() <= 650){
                                if(Flota[i].getY() == 600 ){
                                    Flota[i].setDegree(0d);
                                }
                                Flota[i].setX(Flota[i].getX() + Flota[i].getSpeed());
                            }else if(Flota[i].getX() >= 650 && Flota[i].getY() <= 600 && Flota[i].getY() > 500 && Flota[i].getDegree() < 90){
                                Flota[i].setDegree(Flota[i].getDegree() + (Flota[i].getSpeed()));
                                radDegree = (Math.PI * Flota[i].getDegree())/180;
                                Flota[i].setX((int) (650d + radius2 * Math.sin(radDegree)));
                                Flota[i].setY((int) (550d + radius2 * Math.cos(radDegree)));
                            }else if(Flota[i].getX() >= 650 && Flota[i].getDegree() == 90){
                                Flota[i].setY(550);
                                Flota[i].setX(700);
                                Flota[i].setlaneChange(false);
                                Flota[i].setLane(true);
                                Flota[i].setDegree(0);
                                System.out.println("Samolot nr" + i + " [" + Flota[i].getX() + ", " + Flota[i].getY() + "] Lane: " + Flota[i].getLane() + " ChLane: " + Flota[i].ifChange());
                            }
                        }
                    } 
                }
            }
        repaint();
        }
    }
    
    public class plane{
        private final int   wsp[] = new int[2];
        private double      degree;     //kąt skretu        
        private boolean     lane;       //trasa wlotu
        private boolean     laneChange; //zmiana trasy
        private boolean     position;
        private int         speed;
        private boolean     collision;
        private int         xChange;
        private int         yChange;

        public plane(int x, int y, boolean iLane){
            wsp[0] =    x;
            wsp[1] =    y;
            lane =      iLane;      //pas na którym się znajduje
            position =  true;       //w powietrzu
            xChange = 0;            //współrzędne do zawracania
            yChange = 0;            //współrzędne do zawracania
            laneChange = false;
            degree =    0;          //do zmian pasa i ladowania //zalezne od lane - do zrobienia
            collision = false;      //czy zderzenie
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
        public int      getxChanege(){
            return xChange;
        }        
        public int      getyChanege(){
            return yChange;
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
        public boolean  ifChange(){
            return laneChange;
        }
    //Ustawianie
        public void     setX(int iX){
            wsp[0] = iX;
        }        
        public void     setY(int iY){
            wsp[1] = iY;
        }
        public void     setLane(boolean aLane){
            lane = aLane;
        }        
        public void     changePosition( boolean iPosition){
            position = iPosition;
        }
        public void     setxChange(int ixChange){
            xChange = ixChange;
        }        
        public void     setyChange(int iyChange){
            yChange = iyChange;
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
        public void     setlaneChange(boolean ilaneChange){
            laneChange = ilaneChange;
        }
    }

    AirPort(){
        //Okno
        super("Kontroler Lotów");
        setBounds(10, 10, 800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
        createBufferStrategy(2);

        menu =      new ImageIcon("Obrazki/menu.png").getImage();
        lotnisko =  new ImageIcon("Obrazki/lotnisko.png").getImage();
        samolot =   new ImageIcon("Obrazki/samolot.png").getImage();      
        

        Flota = new plane[6];
        for(int i = 0; i < Flota.length; i++){
            boolean iLane = Math.random() < 0.5;
            Random rand = new Random();            
            int iRand = 50 * (rand.nextInt(5)+1);
            if(iLane){
                Flota[i] = new plane(700, 800 + i * iRand * level, iLane);
            }else{
                Flota[i] = new plane(800 + i * iRand * level, 700, iLane);
            }
        System.out.println("Samolot nr" + i + " [" + Flota[i].getX() + "," + Flota[i].getY() + "] " + Flota[i].getLane());    
        }
        
        horizontalParking = new int[3];
        for(int i = 0; i < horizontalParking.length; i++){
            horizontalParking[i] = 10;
        }
        
        verticalParking   = new int[3];
        for(int i = 0; i < verticalParking.length; i++){
            verticalParking[i] = 10;
        }        
        
        zegar = new Timer();
        zegar.scheduleAtFixedRate(new Dzialanie(), 0, 20);   
    }
     
    @Override
    public void paint(Graphics g){
        BufferStrategy bstrategy = this.getBufferStrategy();
        Graphics2D g2D = (Graphics2D) bstrategy.getDrawGraphics();
        
        if(start){ //Start gry
            g2D.drawImage(lotnisko, 0,  26, null);   // Nie wiem dlaczego ale obcina 26px z góry.
            for(int i = 0; i < Flota.length; i++ ){
//                g2D.rotate(0,Flota[i].getX() + 15,  Flota[i].getY() + 26 + 15);
//                if(Flota[i].getLane()){
//                    if(Flota[i].getX() >= 600 && Flota[i].getY() >= 460){
//                       g2D.rotate(Math.PI/2, Flota[i].getX() + 15,  Flota[i].getY() + 26 + 15);
//                    }else if(Flota[i].getX() > 600 && Flota[i].getY() < 460 && Flota[i].getY() > 350 && Flota[i].getDegree() <= 90 ){
//                       g2D.rotate((Math.PI * Flota[i].getDegree())/180, Flota[i].getX() + 15,  Flota[i].getY() + 26 + 15);
//                    }
//                }else{  //drugi pas
//                    if(Flota[i].getX() >= 460 && Flota[i].getY() >= 600){
//                        g2D.rotate(0,Flota[i].getX() + 15,  Flota[i].getY() + 26 + 15);
//                        //g2D.drawImage(samolot,  Flota[i].getX(),  Flota[i].getY() + 26, null); 
//                    }else if(Flota[i].getY() > 600 && Flota[i].getX() < 460 && Flota[i].getX() > 350 && Flota[i].getDegree() <= 90){
//                        g2D.rotate((Math.PI * Flota[i].getDegree())/180, Flota[i].getX() + 15,  Flota[i].getY() + 26 + 15);
//                        //g2D.drawImage(samolot,  Flota[i].getX(),  Flota[i].getY() + 26, null); 
//                    }
//                } 
                g2D.drawImage(samolot,  Flota[i].getX(),  Flota[i].getY() + 26, null); 
            }

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {    
                   //System.out.println("X" + e.getX() + "Y:" + e.getY());
                   for(int i = 0; i < Flota.length; i++){
                       //System.out.println("Nr" + i + " X:" + e.getX() + "pX:" + Flota[i].getX() + "| Y:" + e.getY() + "pY: " + Flota[i].getY());
                        if((e.getX() -31) <= Flota[i].getX() && e.getX() >= Flota[i].getX() && (e.getY() -30 -26) <= Flota[i].getY() && e.getY() >= (Flota[i].getY() +26)){
                            Flota[i].setlaneChange(true);
                            Flota[i].setxChange(Flota[i].getX());
                            Flota[i].setyChange(Flota[i].getY());
                            System.out.println("Kliknięto");
                        }                           
                   }
                }
            });
        }else{
            //Rysowanie menu
            g2D.drawImage(menu, 0,  26, null); 
            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {                   
                    if(e.getX() > 574 && e.getX() < 766 && e.getY() < 775 && e.getY() > 651)
                        start = true;
                }
            });
        }
        
        g2D.dispose();
        bstrategy.show();
    }
}
