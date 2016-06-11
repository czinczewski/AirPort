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
    private Image airliner;
    private Timer timer;
    private boolean start       = false;
    private int     level       =   1;      //wprowadzić zmiane poziomów
    private plane[] Fleet;
    private int horizontalParking[];        //parking poziomy
    private int verticalParking[];          //parking pionowy
//    private boolean naparkingu = true;    //dodać do klasy samolot
//    private boolean parking = true;       //dodać do klasy samolot

    
    class Dzialanie extends TimerTask{
        private double  radDegree   =   0;
        private int     radius      = 100;
        private int     radius2     =  50;
        @Override
        public void run(){
            if(start){
                for(int i = 0; i < 6; i++){
                    if(level <= 1){
                        Fleet[i].setSpeed(1);
                    }else if(level == 2){
                        Fleet[i].setSpeed(2);
                    }else if(level >= 3){      //poziom nie do przejścia
                        Fleet[i].setSpeed(4);   //prawdopodobnie trzba mocno zwiększyć prędkość
                    }                           //sprawdzić drogi czy nie wypada z wyjątków
                    
                    if(Fleet[i].getLane()){ //wylot pionowy 
                        if(!Fleet[i].getlaneChange()){ //bez zmiany pasa
                            if(Fleet[i].getX() >= 600 && Fleet[i].getY() >= 460){
                                Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());
                            }else if(Fleet[i].getX() >= 600 && Fleet[i].getY() <= 460 && Fleet[i].getY() >= 360 && Fleet[i].getDegree() < 90 ){
                                Fleet[i].setDegree(Fleet[i].getDegree() + (Fleet[i].getSpeed()));
                                radDegree = (Math.PI * Fleet[i].getDegree())/180;
                                Fleet[i].setX((int) (600d + radius * Math.cos(radDegree)));
                                Fleet[i].setY((int) (460d - radius * Math.sin(radDegree)));
                            }else if(Fleet[i].getX() <= 600 && Fleet[i].getX() > 115 && Fleet[i].getY() <= 360 ){
                                Fleet[i].changePosition(false); //wylondowałem
                                Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());
                            }else if(Fleet[i].getX() <= 115 && Fleet[i].getY() <=360 && Fleet[i].getY() >= 300 && Fleet[i].getDegree() <= 180){
                                Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                                if(Fleet[i].getDegree() >= 180){
                                    Fleet[i].setDegree(180d);
                                    Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());                                    
                                }
                            }else if(Fleet[i].getY() <= 300 && Fleet[i].getY() >= 200){ //PARKOWANIE
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
                            if(Fleet[i].getY() >= 600 && Fleet[i].getX() >= 700){
                                Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());
                                Fleet[i].setxChange(700);
                                Fleet[i].setyChange(600);
                            }else if(Fleet[i].getX() > 600){ //dodać warunek na pozycje Y
                                Fleet[i].setDegree(Fleet[i].getDegree() + (Fleet[i].getSpeed()));
                                radDegree = (Math.PI * Fleet[i].getDegree())/180;
                                Fleet[i].setX((int) (650d + radius2 * Math.cos(radDegree)));
                                Fleet[i].setY((int) (Fleet[i].getyChanege() - radius2 * Math.sin(radDegree)));
                            }else if(Fleet[i].getX() == 600 && Fleet[i].getY() <= 650){
                                if(Fleet[i].getX() == 600 ){
                                    Fleet[i].setDegree(0d);
                                }
                                Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                            }else if(Fleet[i].getY() >= 650 && Fleet[i].getX() <= 600 && Fleet[i].getX() > 500 && Fleet[i].getDegree() < 90){
                                Fleet[i].setDegree(Fleet[i].getDegree() + (Fleet[i].getSpeed()));
                                radDegree = (Math.PI * Fleet[i].getDegree())/180;
                                Fleet[i].setX((int) (550d + radius2 * Math.cos(radDegree)));
                                Fleet[i].setY((int) (650d + radius2 * Math.sin(radDegree)));
                            }else if(Fleet[i].getY() >= 650 && Fleet[i].getDegree() == 90){
                                Fleet[i].setY(700);
                                Fleet[i].setX(550);
                                Fleet[i].setlaneChange(false);
                                Fleet[i].setLane(false);
                                Fleet[i].setDegree(0);
                            }
                        }
                    }else{  //wylot poziomy
                        if(Fleet[i].getlaneChange() == false){ //be zmiany pasa
                            if(Fleet[i].getX() >= 460 && Fleet[i].getY() >= 600){
                                Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());                                
                            }else if(Fleet[i].getY() >= 600 && Fleet[i].getX() <= 460 && Fleet[i].getX() >= 360 && Fleet[i].getDegree() <= 90){
                                Fleet[i].setDegree(Fleet[i].getDegree() + (Fleet[i].getSpeed()));
                                radDegree = (Math.PI * Fleet[i].getDegree())/180;
                                Fleet[i].setX((int) (460d - radius * Math.sin(radDegree)));
                                Fleet[i].setY((int) (600d + radius * Math.cos(radDegree)));
                            }else if(Fleet[i].getY() < 600 && Fleet[i].getY() > 115 && Fleet[i].getX() <= 360 ){
                                Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());
                            }else if(Fleet[i].getY() <= 115 && Fleet[i].getX() <= 360 && Fleet[i].getX() >= 300 && Fleet[i].getDegree() <= 180){
                                Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                                if(Fleet[i].getDegree() >= 180){
                                    Fleet[i].setDegree(180d);
                                    Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());                                    
                                }
                            }
                        }else{ //zmiana pasa
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
                                    Fleet[i].setDegree(0d);
                                }
                                Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                            }else if(Fleet[i].getX() >= 650 && Fleet[i].getY() <= 600 && Fleet[i].getY() > 500 && Fleet[i].getDegree() < 90){
                                Fleet[i].setDegree(Fleet[i].getDegree() + (Fleet[i].getSpeed()));
                                radDegree = (Math.PI * Fleet[i].getDegree())/180;
                                Fleet[i].setX((int) (650d + radius2 * Math.sin(radDegree)));
                                Fleet[i].setY((int) (550d + radius2 * Math.cos(radDegree)));
                            }else if(Fleet[i].getX() >= 650 && Fleet[i].getDegree() == 90){
                                Fleet[i].setY(550);
                                Fleet[i].setX(700);
                                Fleet[i].setlaneChange(false);
                                Fleet[i].setLane(true);
                                Fleet[i].setDegree(0);
                            }
                        }
                    } 
                }
                for(int j = 0; j < Fleet.length; j++){
                    for(int k = 0; k < Fleet.length; k++){
                        if(Fleet[j].getPosition() && Fleet[k].getPosition()){
                            if(Fleet[j].getX() >= Fleet[k].getX() && Fleet[j].getX() <= (Fleet[k].getX() + 30)){ //jeszcze warunki na Y
                                Fleet[j].setCollision(true);
                                Fleet[k].setCollision(true);
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
        private boolean     position;   //czy wylądował
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

        loadImages();
        newFlota();
        newParking();
        timer = new Timer();
        timer.scheduleAtFixedRate(new Dzialanie(), 0, 20);   
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
    }
    public void loadImages(){
        menu =      new ImageIcon("Obrazki/menu.png").getImage();
        lotnisko =  new ImageIcon("Obrazki/lotnisko.png").getImage();
        airliner =   new ImageIcon("Obrazki/samolot.png").getImage();
    }
    
    @Override
    public void paint(Graphics g){
        BufferStrategy bstrategy = this.getBufferStrategy();
        Graphics2D g2D = (Graphics2D) bstrategy.getDrawGraphics();
        
        if(!start){//Rysowanie menu
            g2D.drawImage(menu, 0,  26, null);      // Obcina 26px z góry.
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(e.getX() > 574 && e.getX() < 766 && e.getY() < 775 && e.getY() > 651)
                        start = true;
                }
            });
        }else{//Start gry
            g2D.drawImage(lotnisko, 0,  26, null);   // Obcina 26px z góry.
            for(int i = 0; i < Fleet.length; i++ ){
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
                g2D.drawImage(airliner,  Fleet[i].getX(),  Fleet[i].getY() + 26, null); 
                Color pedzel = new Color(255,0,0); g2D.setColor(pedzel);
                g2D.drawOval(Fleet[i].getX(),Fleet[i].getY() +26,30,30);
            }

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {    
                   for(int i = 0; i < Fleet.length; i++){
                        if((e.getX() -31) <= Fleet[i].getX() && e.getX() >= Fleet[i].getX() && (e.getY() -30 -26) <= Fleet[i].getY() && e.getY() >= (Fleet[i].getY() +26)){
                            Fleet[i].setlaneChange(true);   // nie wiadomo dlaczego wchodzi w pętle wielokrotnie //docelowo zamiast TRUE ma być Fleet[i].getlaneChange()
                                                            // przepisać e.getX i e.getY do INT i po spełnieniu if zerować.
                            Fleet[i].setxChange(Fleet[i].getX());
                            Fleet[i].setyChange(Fleet[i].getY());
                            System.out.println("Nr" + i + " " + Fleet[i].getlaneChange());
                        }                           
                   }
                }
            });
        }
        g2D.dispose();
        bstrategy.show();
    }
}
