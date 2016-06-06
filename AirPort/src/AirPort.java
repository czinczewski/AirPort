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
    private final   plane[] horizontalParking = new plane[3];    //parking poziomy
    private final   plane[] verticalParking   = new plane[3];    //parking pionowy
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
                        if(Flota[i].getlaneChange() == false){ //be zmiany pasa
                            if(Flota[i].getX() >= 600 && Flota[i].getY() >= 460){
                                Flota[i].setY(Flota[i].getY() - Flota[i].getSpeed());
                                if(Flota[i].getY() == 500 ){Flota[i].setlaneChange(true);}//Koniec testu
                            }else if(Flota[i].getX() >= 600 && Flota[i].getY() <= 460 && Flota[i].getY() >= 360 && Flota[i].getDegree() < 90 ){
                                Flota[i].setDegree(Flota[i].getDegree() + (Flota[i].getSpeed()));
                                radDegree = (Math.PI * Flota[i].getDegree())/180;
                                Flota[i].setX((int) (600d + radius * Math.cos(radDegree)));
                                Flota[i].setY((int) (460d - radius * Math.sin(radDegree)));
                            }else if(Flota[i].getX() <= 600 && Flota[i].getX() > 115 && Flota[i].getY() <= 360 ){
                                Flota[i].changePosition(false); //już na ziemi
                                Flota[i].setX(Flota[i].getX() - Flota[i].getSpeed());
                            }else if(Flota[i].getX() <= 115 && Flota[i].getY() <=360 && Flota[i].getY() >= 300 && Flota[i].getDegree() <= 180){
                                Flota[i].setDegree(Flota[i].getDegree() + Flota[i].getSpeed());
                                if(Flota[i].getDegree() >= 180){
                                    Flota[i].setDegree(180d);
                                    Flota[i].setY(Flota[i].getY() - Flota[i].getSpeed());                                    
                                }
                            }else if(Flota[i].getY() <= 300 && Flota[i].getY() >= 200){
                                //wybór miejsca na parkingu poziomym
                                //dojazd na miejsce parkingowe
                                //przypisanie do miejsca na parkingu
                            }
                        } else { //Po zmianie kliknieciu pasa
                            //dodać if za nisko na ekranie - leć jeszcze prosto
                            if(Flota[i].getX() > 600){ //dodać warunek na pozycje Y
                                Flota[i].setDegree(Flota[i].getDegree() + (Flota[i].getSpeed()));
                                radDegree = (Math.PI * Flota[i].getDegree())/180;
                                Flota[i].setX((int) (650d + radius2 * Math.cos(radDegree)));
                                Flota[i].setY((int) (500d - radius2 * Math.sin(radDegree))); // zmienne 500d - zrobić - POBRANE Z KLIKNIECIE MYSZKĄ
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
                                System.out.println("Samolot nr" + i + " [" + Flota[i].getX() + ", " + Flota[i].getY() + "] Lane: " + Flota[i].getLane() + " ChLane: " + Flota[i].getlaneChange());
                            }
                        }
                    }else{  //wylot poziomy
                        if(Flota[i].getlaneChange() == false){ //be zmiany pasa
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
                            // ZROBIĆ !!!!
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
        private double      degree;     //kąt skretu        
        private boolean     lane;       //trasa wlotu
        private boolean     laneChange; //zmiana trasy
        private boolean     position;
        private int         speed;
        private boolean     collision;

        public plane(int x, int y, boolean iLane){
            wsp[0] =    x;
            wsp[1] =    y;
            lane =      iLane;
            laneChange = false;
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
        public boolean  getlaneChange(){
            return laneChange;
        }
    //Ustawianie
        public void     setX(int iX){
            wsp[0] = iX;
        }        
        public void     setY(int iY){
            wsp[1] = iY;
        }
        public void     changeLane(boolean aLane){
            lane = aLane;
        }        
        public void     setLane(boolean ilaneChange){
            lane = ilaneChange;
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
        
        zegar = new Timer();
        zegar.scheduleAtFixedRate(new Dzialanie(), 0, 10);   
    }
     
    @Override
    public void paint(Graphics g){
        BufferStrategy bstrategy = this.getBufferStrategy();
        Graphics2D g2D = (Graphics2D) bstrategy.getDrawGraphics();
        
        if(start){
            //Jeśli zaczęto grę rysowanie lotniska
            g2D.drawImage(lotnisko, 0,  26, null);   // Nie wiem dlaczego ale obcina 26px z góry.
            for(int i = 0; i < 6; i++ ){
                //g2D.rotate(0,Flota[i].getX() + 15,  Flota[i].getY() + 26 + 15);
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
                
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {    
                        int place = e.getX();
                    //   if(place > Flota[i].getX());
                    }
                });
            }
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
