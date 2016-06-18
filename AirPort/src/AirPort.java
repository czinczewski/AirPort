import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
    //import java.awt.geom.AffineTransform;
    //import java.awt.image.AffineTransformOp;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
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
    
    private plane[] Fleet;
    private int horizontalParking[], verticalParking[];        //parking poziomy i pionowy
    private int horizontal[], vertical[];                    //listy do wyjazdu
    private int horizontalStart[], verticalStart[];          //listy przed odlotem
    private Image Menu, GameOver, Lotnisko;
    private BufferedImage plane1, plane2;
        
    private Timer timer;
    private boolean start       = false;
    private boolean gameover    = false;
    private int     level       =   1;      //wprowadzić zmiane poziomów
    
    class Dzialanie extends TimerTask{
        private double  radDegree   =   0;
        private int     radius      = 100;
        private int     radius2     =  50;
        @Override
        public  void run(){
            if(start){
                for(int i = 0; i < 6; i++){         
                    setLevel(i); //przypisanie prędkości zgodnie z poziomem
                    if(Fleet[i].getLane()){ //PRZYLOT PIONOWY (z dołu)
                        if(!Fleet[i].getlaneChange()){
                            comingVertical(i);      //przylot normalny
                        }else{
                            changeVertical(i);      //zmiana pasa
                        }
                    }else{                  //PRZYLOT POZIOMY (z prawej)
                        if(Fleet[i].getlaneChange() == false){ 
                            comingHorizontal(i);    //przylot normalny
                        }else{ 
                           changeHorizontal(i);     //zmiana pasa
                        }
                    }
                    detectionCollision(i);
                }
            }
        repaint();
        }
        private void setLevel(int i){
            if(level <= 1){
                Fleet[i].setSpeed(1);
            }else if(level == 2){
                Fleet[i].setSpeed(2);
            }else if(level >= 3){      //poziom nie do przejścia
                Fleet[i].setSpeed(4);   
            }                           
        }
        private void comingVertical(int i){
            if(Fleet[i].getX() >= 600 && Fleet[i].getY() >= 460){
                Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());
            }else if(Fleet[i].getX() >= 600 && Fleet[i].getY() <= 460 && Fleet[i].getY() >= 360 
                    && Fleet[i].getDegree() > -90 && !Fleet[i].getStart()){
                Fleet[i].setDegree(Fleet[i].getDegree() - (Fleet[i].getSpeed()));
                radDegree = (Math.PI * Fleet[i].getDegree())/180;
                Fleet[i].setX((int) (600d + radius * Math.cos(radDegree)));
                Fleet[i].setY((int) (460d + radius * Math.sin(radDegree)));
            }else if(Fleet[i].getX() <= 600 && Fleet[i].getX() > 115 && Fleet[i].getY() <= 360  
                    && Fleet[i].getY() >= 320 && !Fleet[i].getStart()){
                Fleet[i].changePosition(true); //wylądowałem
                Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());
            }else if(Fleet[i].getX() <= 115 && Fleet[i].getY() <=360 && Fleet[i].getY() >= 320 
                    && Fleet[i].getDegree() <= 0 && !Fleet[i].getStart()){
                Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                if(Fleet[i].getDegree() >= 0){
                    Fleet[i].setDegree(0d);
                    Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());                                    
                }
            }else if(Fleet[i].getY() < 320 && Fleet[i].getY() >= 215 && !Fleet[i].getStart()){ //PARKOWANIE
                for(int j = 0; j < 3; j++){
                    if(horizontalParking[j] == 10 && !Fleet[i].getParked1()){
                        horizontalParking[j] = i;
                        Fleet[i].setParked1(true);
                        System.out.println("horizontalParking " + j + " sam nr:" + i);
                    }
                }    
                if( i == horizontalParking[0]){
                    if(Fleet[i].getY() > 300){
                        Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());
                    }else{
                        if(Fleet[i].getX() >= 100){
                            Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());                                        
                        }else{
                             Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());                                           
                        }
                    }
                }
                if( i == horizontalParking[1]){
                    if(Fleet[i].getY() > 300){
                        Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());
                    }else{
                        if(Fleet[i].getX() <= 140){
                            Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());                                        
                        }else{
                             Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());                                           
                        }
                    }
                }
                if( i == horizontalParking[2]){
                    if(Fleet[i].getY() > 300){
                        Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());
                    }else{
                        if(Fleet[i].getX() <= 180){
                            Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());                                        
                        }else{
                             Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());                                           
                        }
                    }
                }
            }else if(Fleet[i].getLoad() < 500 && Fleet[i].getParked1() && !Fleet[i].getStart()){ //Wsiadanie pasażerów
                Fleet[i].setLoad(Fleet[i].getLoad() + Fleet[i].getSpeed());
            }else if(Fleet[i].getStart() && Fleet[i].getLoad() >= 500 && !Fleet[i].getCross()){
                
                
                for(int j = 0; j < 3; j++){
                    if(horizontal[j] == 10 && !Fleet[i].getParked2()){
                        horizontal[j] = i;
                        Fleet[i].setParked2(true);
                        System.out.println("Horizontal " + j + " sam nr:" + i);
                    }
                }
                if(horizontal[0] == i){
                    if(Fleet[i].getY() <= 260){
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                    }else if(Fleet[i].getX() <= 195){
                        Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                    }else if(Fleet[i].getX() >= 195 && Fleet[i].getY() <= 320){
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                        if(Fleet[i].getParked1()){
                            Fleet[i].setParked1(false);
                            for(int j = 0; j < 3; j++){
                                if(horizontalParking[j] == i){
                                    horizontalParking[j] = 10;
                                    System.out.println("Opusciłem2 mijsce nr: " + j);
                                }
                            }
                        }
                    }
                    if(Fleet[i].getX() > 192 && Fleet[i].getY() <= 320 && !Fleet[i].getParked1()){
                        if(Fleet[i].getX() > 190 ){
                            Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());
                        }else{
                            Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                        }
                            
                    }
                }
                if(horizontal[1] == i){
                    if(Fleet[i].getY() <= 260){
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                    }else if(Fleet[i].getX() <= 230){
                        Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                    }else if(Fleet[i].getX() >= 230 && Fleet[i].getY() <= 295){
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                        if(Fleet[i].getParked1()){
                            Fleet[i].setParked1(false);
                            for(int j = 0; j < 3; j++){
                                if(horizontalParking[j] == i){
                                    horizontalParking[j] = 10;
                                    System.out.println("Opusciłem2 mijsce nr: " + j);
                                }
                            }
                        }
                    }
                    if(Fleet[i].getX() > 231 || Fleet[i].getY() > 295){
                        if(Fleet[i].getX() > 231 ){
                            Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());
                        }
                    }
                }
                if(horizontal[2] == i){
                    if(Fleet[i].getY() <= 260){
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                    }else if(Fleet[i].getX() <= 265){
                        Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                    }else if(Fleet[i].getX() >= 265 && Fleet[i].getY() <= 295){
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                        if(Fleet[i].getParked1()){
                            Fleet[i].setParked1(false);
                            for(int j = 0; j < 3; j++){
                                if(horizontalParking[j] == i){
                                    horizontalParking[j] = 10;
                                    System.out.println("Opusciłem2 mijsce nr: " + j);
                                }
                            }
                        }
                    }
                }
            }else if(Fleet[i].getCross() && Fleet[i].getStart()){//kolejna faza
                if(Fleet[i].getParked2()){
                    if(horizontal[0] == 10 && horizontal[1] != 10){
                        if(horizontal[2] != 10){
                            horizontal[0] = horizontal[1];
                            horizontal[1] = horizontal[2];
                            horizontal[2] = 10;
                        }
                        if(horizontal[2] == 10){
                            horizontal[0] = horizontal[1];
                            horizontal[1] = 10;
                        }
                    }
                }
                if(Fleet[i].getParked2()){
                    if(horizontalStart[0] == 10){
                        if(horizontalStart[1] != 10 && horizontalStart[2] != 10){
                            horizontalStart[0] = horizontalStart[1];
                            horizontalStart[1] = horizontalStart[2];
                            horizontalStart[2] = 10;
                        }
                        if(horizontalStart[1] != 10 && horizontalStart[2] == 10){
                            horizontalStart[0] = horizontalStart[1];
                            horizontalStart[1] = 10;
                        }
                    }
                }
                
                for(int j = 0; j < 3; j++){
                    if(horizontalStart[j] == 10 && !Fleet[i].getParked3()){
                        horizontalStart[j] = i;
                        Fleet[i].setParked3(true);
                        System.out.println("HorizontalStart " + j + " sam nr:" + i);
                    }
                }
                
                if(horizontalStart[0] == i){
                    if(Fleet[i].getY() <= 565){
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                    }else if(Fleet[i].getX() <= 315){
                        Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                        if(Fleet[i].getParked2()){
                            Fleet[i].setParked2(false);
                            for(int j = 0; j < 3; j++){
                                if(horizontalStart[j] == i){
                                    horizontalStart[j] = 10;
                                    System.out.println("Opusciłem3 miejsce nr: " + j);
                                }
                            }
                        }
                    }
                }
                if(horizontalStart[1] == i){
                    if(Fleet[i].getY() <= 565){
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                    }else if(Fleet[i].getX() <= 280){
                        Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                        if(Fleet[i].getParked2()){
                            Fleet[i].setParked2(false);
                            for(int j = 0; j < 3; j++){
                                if(horizontalStart[j] == i){
                                    horizontalStart[j] = 10;
                                    System.out.println("Opusciłem3 miejsce nr: " + j);
                                }
                            }
                        }
                    }
                }
                if(horizontalStart[2] == i){
                    if(Fleet[i].getY() <= 565){
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                    }else if(Fleet[i].getX() <= 245){
                        Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                        if(Fleet[i].getParked2()){
                            Fleet[i].setParked2(false);
                            for(int j = 0; j < 3; j++){
                                if(horizontalStart[j] == i){
                                    horizontalStart[j] = 10;
                                    System.out.println("Opusciłem3 miejsce nr: " + j);
                                }
                            }
                        }
                    }
                }                
            }
        }
        private void changeVertical(int i){
            if(Fleet[i].getY() >= 600 && Fleet[i].getX() >= 700){
                Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());
                Fleet[i].setxChange(700);
                Fleet[i].setyChange(600);
            }else if(Fleet[i].getX() > 600){ //dodać warunek na pozycje Y
                Fleet[i].setDegree(Fleet[i].getDegree() - (Fleet[i].getSpeed()));
                radDegree = (Math.PI * Fleet[i].getDegree())/180;
                Fleet[i].setX((int) (650d + radius2 * Math.cos(radDegree)));
                Fleet[i].setY((int) (Fleet[i].getyChanege() + radius2 * Math.sin(radDegree)));
            }else if(Fleet[i].getX() == 600 && Fleet[i].getY() <= 650){
                if(Fleet[i].getX() == 600 ){
                    Fleet[i].setDegree(-180d);
                }
                Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
            }else if(Fleet[i].getY() >= 650 && Fleet[i].getX() <= 600 && Fleet[i].getX() > 500 && Fleet[i].getDegree() < -90){
                Fleet[i].setDegree(Fleet[i].getDegree() + (Fleet[i].getSpeed()));
                radDegree = (Math.PI * Fleet[i].getDegree())/180;
                Fleet[i].setX((int) (550d - radius2 * Math.cos(radDegree)));
                Fleet[i].setY((int) (650d - radius2 * Math.sin(radDegree)));
            }else if(Fleet[i].getY() >= 650 && Fleet[i].getDegree() == -90 && !Fleet[i].getStart()){
                Fleet[i].setY(700);
                Fleet[i].setX(550);
                Fleet[i].setlaneChange(false);
                Fleet[i].setLane(false);
                Fleet[i].setDegree(0);
            }
        }
        private void comingHorizontal(int i){
            if(Fleet[i].getX() >= 460 && Fleet[i].getY() >= 600 && !Fleet[i].getStart()){
                Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());                                
            }else if(Fleet[i].getY() >= 600 && Fleet[i].getX() <= 460 && Fleet[i].getX() >= 360 && Fleet[i].getDegree() <= 90 && !Fleet[i].getStart()){
                Fleet[i].setDegree(Fleet[i].getDegree() + (Fleet[i].getSpeed()));
                radDegree = (Math.PI * Fleet[i].getDegree())/180;
                Fleet[i].setX((int) (460d - radius * Math.sin(radDegree)));
                Fleet[i].setY((int) (600d + radius * Math.cos(radDegree)));
            }else if(Fleet[i].getY() < 600 && Fleet[i].getY() > 115 && Fleet[i].getX() <= 360 && Fleet[i].getX() >= 320 && !Fleet[i].getStart()){
                Fleet[i].changePosition(true); //wylądowałem
                Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());
            }else if(Fleet[i].getY() <= 115 && Fleet[i].getX() <= 360 && Fleet[i].getX() >= 320 && Fleet[i].getDegree() <= 91 && !Fleet[i].getStart()){
                Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                if(Fleet[i].getDegree() <= 0){
                    Fleet[i].setDegree(0d);
                    Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());                                    
                }
            }else if(Fleet[i].getX() < 320 && Fleet[i].getX() >= 215 && !Fleet[i].getStart()){ //PARKOWANIE
                for(int j = 0; j < 3; j++){
                    if(verticalParking[j] == 10 && !Fleet[i].getParked1()){
                        verticalParking[j] = i;
                        Fleet[i].setParked1(true);
                        System.out.println("Pion" + j + " nr sam: " + i);
                    }

                    if( i == verticalParking[0]){
                    if(Fleet[i].getX() > 300){
                        Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());
                    }else{
                        if(Fleet[i].getY() >= 100){
                            Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());                                        
                        }else{
                             Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());                                           
                        }
                    }
                }
                if( i == verticalParking[1]){
                    if(Fleet[i].getX() > 300){
                        Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());
                    }else{
                        if(Fleet[i].getY() <= 140){
                            Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());                                        
                        }else{
                             Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());                                           
                        }
                    }
                }
                if( i == verticalParking[2]){
                    if(Fleet[i].getX() > 300){
                        Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());
                    }else{
                        if(Fleet[i].getY() <= 180){
                            Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());                                        
                        }else{
                             Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());                                           
                        }
                    }
                }
                }
            }else if(Fleet[i].getLoad() < 500 && !Fleet[i].getStart()){
                Fleet[i].setLoad(Fleet[i].getLoad() + Fleet[i].getSpeed());
            }
        }
        private void changeHorizontal(int i){
             if(Fleet[i].getX() >= 600 && Fleet[i].getY() >= 700){
                Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());
                Fleet[i].setxChange(600);
                Fleet[i].setyChange(700);
            }else if(Fleet[i].getY() > 600){ 
                Fleet[i].setDegree(Fleet[i].getDegree() + (Fleet[i].getSpeed()));
                radDegree = (Math.PI * Fleet[i].getDegree())/180;
                Fleet[i].setX((int) (Fleet[i].getxChanege() - radius2 * Math.sin(radDegree)));
                Fleet[i].setY((int) (650d + radius2 * Math.cos(radDegree))); 
            }else if(Fleet[i].getY() == 600 && Fleet[i].getX() <= 650){
                if(Fleet[i].getY() == 600 ){
                    Fleet[i].setDegree(-180d);
                }
                Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
            }else if(Fleet[i].getX() >= 650 && Fleet[i].getY() <= 600 && Fleet[i].getY() > 500 && Fleet[i].getDegree() > -270){
                Fleet[i].setDegree(Fleet[i].getDegree() - (Fleet[i].getSpeed()));
                radDegree = (Math.PI * Fleet[i].getDegree())/180;
                Fleet[i].setX((int) (650d + radius2 * Math.sin(radDegree)));
                Fleet[i].setY((int) (550d - radius2 * Math.cos(radDegree)));
                System.out.println("NIE WSZEDLEM");
            }else if(Fleet[i].getX() >= 650 && Fleet[i].getDegree() == -270){
                Fleet[i].setY(550);
                Fleet[i].setX(700);
                Fleet[i].setlaneChange(false);
                Fleet[i].setLane(true);
                Fleet[i].setDegree(0);
            }
        }
        private void detectionCollision(int i){
            for(int j = 0; j < Fleet.length; j++){ //KOLIZJE 
                for(int k = 0; k < Fleet.length; k++){
                    if(Fleet[j].getPosition() && Fleet[k].getPosition() && !Fleet[k].getCollision() && j != k){
                        if(Fleet[j].getX() >= Fleet[k].getX() && Fleet[j].getX() <= (Fleet[k].getX() + 30) && Fleet[j].getY() >= Fleet[k].getY() && Fleet[j].getY() <= (Fleet[k].getY() + 30 )){
                            Fleet[j].setCollision(true);
                            Fleet[k].setCollision(true);
//                            System.out.println("Nr " + j + " zderzenie z nr " + k);
//                            System.out.println("[" +Fleet[j].getX() + "," + Fleet[j].getY() + "]   [" +Fleet[k].getX() + "," + Fleet[k].getY() + "]" );
//                            gameover = true;
//                            start = false;
//                            newFlota();
                        }
                    }
                }
            }
        }
    }

    public class plane{
        private final int   wsp[] = new int[2];
        private double      degree;     //kąt skretu        
        private boolean     lane;       //trasa wlotu
        private int         speed;  //prędkość
        private boolean     collision;  //czy się zderzył
        private boolean     laneChange;         //zmiana trasy
        private int         xChange, yChange;   //dane do skrętu
        private boolean     position;   //czy wylądował
        private boolean     parked1, parked2, parked3;     //czy zaparkował
        private int         load;               //wsiadanie pasażerów
        private boolean     start, cross, fly;  //odlot 

        public plane(int x, int y, boolean iLane){
            wsp[0] =    x;
            wsp[1] =    y;
            lane =      iLane;      //pas na którym się znajduje
            degree =    0;          //do zmian pasa i ladowania
            laneChange = false;
            xChange = 0;            //współrzędne do zawracania
            yChange = 0;            //współrzędne do zawracania
            position =  false;      //czy wylądował
            collision = false;      //czy zderzenie
            parked1 = false; parked2 = false; parked3 = false; load = 0;
            start = false;  cross = false; fly = false;
        }
    //Sprawdzanie     
        public int      getX        (){
            return wsp[0];
        }        
        public int      getY        (){
            return wsp[1];
        }       
        public boolean  getLane     (){
            return lane;
        }        
        public boolean  getPosition (){
            return position;
        }
        public int      getxChanege (){
            return xChange;
        }        
        public int      getyChanege (){
            return yChange;
        }       
        public int      getSpeed    (){
            return speed;
        }
        public double   getDegree   (){
            return degree;
        }
        public boolean  getCollision(){
            return collision;
        }
        public boolean  getlaneChange(){
            return laneChange;
        }
        public boolean  getParked1   (){
            return parked1;
        }
        public boolean  getParked2   (){
            return parked2;
        }
        public boolean  getParked3   (){
            return parked3;
        }
        public int      getLoad     (){
            return load;
        }
        public boolean  getStart    (){
            return start;
        }
        public boolean  getCross    (){
            return cross;
        }
        public boolean  getFly      (){
            return fly;
        }
    //Ustawianie
        public void     setX        (int iX){
            wsp[0] = iX;
        }        
        public void     setY        (int iY){
            wsp[1] = iY;
        }
        public void     setLane     (boolean aLane){
            lane = aLane;
        }        
        public void     changePosition(boolean iPosition){
            position = iPosition;
        }
        public void     setxChange  (int ixChange){
            xChange = ixChange;
        }        
        public void     setyChange  (int iyChange){
            yChange = iyChange;
        }
        public void     setSpeed    (int iSpeed){
            speed = iSpeed;
        }
        public void     setDegree   (double iDegree){
            degree = iDegree;
        }
        public void     setCollision(boolean iCollision){
            collision = iCollision;
        }
        public void     setlaneChange(boolean ilaneChange){
            laneChange = ilaneChange;
        }
        public void     setParked1   (boolean iParked){
            parked1 = iParked;
        }
        public void     setParked2   (boolean iParked){
            parked2 = iParked;
        }
        public void     setParked3   (boolean iParked){
            parked3 = iParked;
        }
        public void     setLoad     (int iLoad){
            load = iLoad;
        }
        public void     setStart    (boolean iStart){
            start = iStart;
        }
        public void     setCross    (boolean iCross){
            cross = iCross;
        }
        public void     setFly      (boolean iFly){
            fly = iFly;
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

        loadImages();
        newFlota();
        newParking();
        timer = new Timer();
        timer.scheduleAtFixedRate(new Dzialanie(), 0, 10);   
    }
    public void newFlota(){
        Fleet = new plane[6];
        for(int i = 0; i < Fleet.length; i++){
            boolean iLane = Math.random() < 0.5;
            Random rand = new Random();            
            int iRand = 50 * (rand.nextInt(5)+1);
            if(iLane){
                Fleet[i] = new plane(700, 800 + i * iRand * level, iLane);
            }else{
                Fleet[i] = new plane(800 + i * iRand * level, 700, iLane);
            }
        System.out.println("Samolot nr" + i + " [" + Fleet[i].getX() + "," + Fleet[i].getY() + "] " + Fleet[i].getLane());    
        }
    }
    public void newParking(){
        horizontalParking = new int[3];
            for(int i = 0; i < horizontalParking.length; i++){
                horizontalParking[i] = 10;
            }
        verticalParking   = new int[3];
            for(int i = 0; i < verticalParking.length; i++){
                verticalParking[i] = 10;
            }
        horizontal = new int[3];
            for(int i = 0; i < horizontal.length; i++){
                horizontal[i] = 10;
            }
        vertical   = new int[3];
            for(int i = 0; i < vertical.length; i++){
                vertical[i] = 10;
            }
        horizontalStart = new int[3];
            for(int i = 0; i < horizontalStart.length; i++){
                horizontalStart[i] = 10;
            }
        verticalStart   = new int[3];
            for(int i = 0; i < verticalStart.length; i++){
                verticalParking[i] = 10;
            }
    }
    public void loadImages(){
        Menu = new ImageIcon("Obrazki/menu.png").getImage();
        GameOver =  new ImageIcon("Obrazki/gameover.png").getImage();
        Lotnisko =  new ImageIcon("Obrazki/lotnisko.png").getImage();
        plane1 =  LoadImage("Obrazki/samolot1.png");
        plane2 =  LoadImage("Obrazki/samolot2.png");
    }
    
    BufferedImage LoadImage(String ImageName){
        BufferedImage img = null;
        
        try{
            img = ImageIO.read(new File(ImageName));
        }catch(IOException e){
            
        }
        
        return img;
    }
    
    @Override
    public void paint(Graphics g){
        BufferStrategy bstrategy = this.getBufferStrategy();
        Graphics2D g2D = (Graphics2D) bstrategy.getDrawGraphics();
        
        if(!start){ //Rysowanie menu
            if(!gameover){
                g2D.drawImage(Menu, 0,  26, null);      // Obcina 26px z góry.
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(e.getX() > 574 && e.getX() < 766 && e.getY() < 775 && e.getY() > 651)
                            start = true;
                    }
                });
            }else{  //Game Over
                g2D.drawImage(GameOver, 0,  26, null);  // Obcina 26px z góry.
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(e.getX() > 574 && e.getX() < 766 && e.getY() < 775 && e.getY() > 651)
                            start = true; gameover = false;
                    }
                });
            }
        }else{      //Start gry
            g2D.drawImage(Lotnisko, 0,  26, null);      // Obcina 26px z góry.
            for(int i = 0; i < Fleet.length; i++ ){
//                g2D.rotate(0,Fleet[i].getX() + 15,  Fleet[i].getY() + 26 + 15);
//                if(Fleet[i].getLane()){
//                    if(Fleet[i].getX() >= 600 && Fleet[i].getY() >= 460){
//                       g2D.rotate(Math.PI/2, Fleet[i].getX() + 15,  Fleet[i].getY() + 26 + 15);
//                    }else if(Fleet[i].getX() > 600 && Fleet[i].getY() < 460 && Fleet[i].getY() > 350 && Fleet[i].getDegree() <= 90 ){
//                       g2D.rotate((Math.PI * Fleet[i].getDegree())/180, Fleet[i].getX() + 15,  Fleet[i].getY() + 26 + 15);
//                    }
//                }else{  //drugi pas
//                    if(Fleet[i].getX() >= 460 && Fleet[i].getY() >= 600){
//                        g2D.rotate(0,Fleet[i].getX() + 15,  Fleet[i].getY() + 26 + 15);
//                        //g2D.drawImage(samolot,  Flota[i].getX(),  Flota[i].getY() + 26, null); 
//                    }else if(Fleet[i].getY() > 600 && Fleet[i].getX() < 460 && Fleet[i].getX() > 350 && Fleet[i].getDegree() <= 90){
//                        g2D.rotate((Math.PI * Fleet[i].getDegree())/180, Fleet[i].getX() + 15,  Fleet[i].getY() + 26 + 15);
//                        //g2D.drawImage(samolot,  Flota[i].getX(),  Flota[i].getY() + 26, null); 
//                    }
//                } 
                
                
                if(Fleet[i].getLane()){
                    AffineTransform at1 = AffineTransform.getTranslateInstance(Fleet[i].getX(), Fleet[i].getY()+ 26);
                       at1.translate(15, 15);
                       at1.rotate((Math.PI * Fleet[i].getDegree()/180));
                       at1.translate(-15, -15);
                       g2D.drawImage(plane1,  at1, null);
                }else{  //drugi pas
                    AffineTransform at2 = AffineTransform.getTranslateInstance(Fleet[i].getX(), Fleet[i].getY()+ 26);  
                    
                       at2.translate(15, 15);
                       at2.rotate((Math.PI * Fleet[i].getDegree())/180);
                       at2.translate(-15, -15);
                    g2D.drawImage(plane2,  at2, null);
                } 
                
                
                g2D.drawString(Integer.toString(i), Fleet[i].getX(), Fleet[i].getY() + 26);
//                Raster a;
//                //
//                Shape test;
//                
//                AffineTransformOp op = new AffineTransformOp(new AffineTransform(), HIDE_ON_CLOSE);
//                op.
//                //
                if(Fleet[i].getX() <= 215 && Fleet[i].getY() <= 215 && Fleet[i].getParked1()){
                    Color pedzel1 = new Color(255,255,255); g2D.setColor(pedzel1);
                    g2D.fillRect(Fleet[i].getX(), Fleet[i].getY() +20, 500/17, 5);
                    Color pedzel2 = new Color(255,0,0); g2D.setColor(pedzel2);
                    g2D.fillRect(Fleet[i].getX(), Fleet[i].getY() +20, Fleet[i].getLoad()/17, 5);
                }
            }
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {    
                   for(int i = 0; i < Fleet.length; i++){
                        if((e.getX() -31) <= Fleet[i].getX() && e.getX()>= Fleet[i].getX() 
                                && (e.getY() -30 -26) <= Fleet[i].getY() && e.getY() >= (Fleet[i].getY() +26) 
                                && e.getX() >= 400 && e.getY() >= 400){
                            if(!Fleet[i].getlaneChange()){    
                                Fleet[i].setlaneChange(!Fleet[i].getlaneChange());
                                Fleet[i].setxChange(Fleet[i].getX());
                                Fleet[i].setyChange(Fleet[i].getY());
                            }
                        }
                        if((e.getX() -31) <= Fleet[i].getX() && e.getX()>= Fleet[i].getX() 
                                && (e.getY() -30 -26) <= Fleet[i].getY() && e.getY() >= (Fleet[i].getY() +26) 
                                && e.getX() <= 260 && e.getY() <= (260 + 26) && Fleet[i].getParked1()){
                            if(!Fleet[i].getStart()){    
                                Fleet[i].setStart(!Fleet[i].getStart());
                            }
                        }
                        if((e.getX() -31) <= Fleet[i].getX() && e.getX()>= Fleet[i].getX() 
                                && (e.getY() -30 -26) <= Fleet[i].getY() && e.getY() >= (Fleet[i].getY() +26) 
                                && e.getX() <= 350 && e.getY() <= (350 + 26) && Fleet[i].getParked2() && Fleet[i].getStart()){
                            if(!Fleet[i].getCross()){    
                                Fleet[i].setCross(true);
                                System.out.println("Cross " + i + Fleet[i].getCross());
                            }
                        }
                   }
                }
            });
            g2D.drawRect(0, 26, 260, 260);
            g2D.drawRect(0, 26, 350, 350);
            g2D.drawRect(400, 426, 400, 400);
        }
        g2D.dispose();
        bstrategy.show();
    }
}
