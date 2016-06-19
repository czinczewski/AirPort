import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
    }
//Flota samolotów
    private Plane[] Fleet;
//Parkingi i kolejki na lotnisku
    private int horizontalParking[], verticalParking[], horizontal[], vertical[], horizontalStart[], verticalStart[]; 
//Grafika gry
    private Image Menu, GameOver, Win, Lotnisko;
    private BufferedImage plane1, plane2;
    
    private Timer timer;
    private boolean start = false, gameover = false;
    private boolean timetrial = true;
    private long startTime, endTime;
    private int points;       
    
    class Flight extends TimerTask{
        private double radDegree = 0;
        private int radius = 100, radius2 = 50;
        @Override
        public  void run(){
            if(start){
                for(int i = 0; i < Fleet.length; i++){         
                    if(Fleet[i].getLane()){ //PRZYLOT PIONOWY (z dołu)
                        if(!Fleet[i].getLaneChange()){
                            comingVertical(i);      //przylot normalny
                        }else{
                            changeVertical(i);      //zmiana pasa
                        }
                    }else{                  //PRZYLOT POZIOMY (z prawej)
                        if(Fleet[i].getLaneChange() == false){ 
                            comingHorizontal(i);    //przylot normalny
                        }else{ 
                           changeHorizontal(i);     //zmiana pasa
                        }
                    }
                    detectionCollision();
                }
            }
        repaint();
        }
//przylot pionowy z dołu        
        private void comingVertical     (int i){
            if(Fleet[i].getX() >= 600 && Fleet[i].getY() >= 460){
                Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());
            }else if(Fleet[i].getX() >= 600 && Fleet[i].getY() <= 460 && Fleet[i].getY() >= 360 && Fleet[i].getDegree() > -90 && !Fleet[i].getStart()){
                Fleet[i].setDegree(Fleet[i].getDegree() - (Fleet[i].getSpeed()));
                radDegree = (Math.PI * Fleet[i].getDegree())/180;
                Fleet[i].setX((int) (600d + radius * Math.cos(radDegree)));
                Fleet[i].setY((int) (460d + radius * Math.sin(radDegree)));
            }else if(Fleet[i].getX() <= 600 && Fleet[i].getX() > 115 && Fleet[i].getY() <= 360 && Fleet[i].getY() >= 320 && !Fleet[i].getStart()){
                Fleet[i].changePosition(true); //wylądowałem
                Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());
            }else if(Fleet[i].getX() <= 115 && Fleet[i].getX() >= 100 && Fleet[i].getY() <=360 && Fleet[i].getY() >= 320 && Fleet[i].getDegree() <= 0 && !Fleet[i].getStart()){
                Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                if(Fleet[i].getDegree() >= 0){
                    Fleet[i].setDegree(0d);
                    Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());                                    
                }
            }else if(Fleet[i].getY() <= 320 && Fleet[i].getY() >= 210 &&  Fleet[i].getX() >= 60 &&  Fleet[i].getX() <= 190 && !Fleet[i].getStart()){ //PARKOWANIE 
                parkingVertical(i);
            }else if(Fleet[i].getY() <= 220 && Fleet[i].getY() >= 200 && Fleet[i].getLoad() < 500 && Fleet[i].getParked() && !Fleet[i].getStart()){ //Wsiadanie pasażerów
                Fleet[i].setLoad(Fleet[i].getLoad() + Fleet[i].getSpeed());
            }else if(Fleet[i].getX() <= 320 && Fleet[i].getX() >= 100 && Fleet[i].getY() <= 350 && Fleet[i].getY() >= 190 && Fleet[i].getStart() && Fleet[i].getLoad() >= 500 && !Fleet[i].getCross()){
                crossingVertical(i);
            }else if(Fleet[i].getCross() && Fleet[i].getStart() && !Fleet[i].getFly()){//kolejna faza
                takingOffVertical(i);
            }else if(Fleet[i].getFly() && Fleet[i].getY() < 600 && Fleet[i].getY() > 110 && Fleet[i].getX() <= 360 && Fleet[i].getX() >= 310){
                if(Fleet[i].getX() < 360){
                    Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                }else{
                    if(Fleet[i].getTakingOff()){
                        Fleet[i].setTakingOff(false);
                        for(int j = 0; j < 3; j++){
                            if(horizontalStart[j] == i){
                                horizontalStart[j] = 10;
                                System.out.println("Opusciłem horizontalStart_" + j + " sam nr:" + i);
                            }
                        }
                        if(horizontalStart[0] == 10 && horizontalStart[1] != 10){
                            System.out.println("Przemieszczam horizontalStart_[" + horizontalStart[0] +" "+ horizontalStart[1]+" "+ horizontalStart[2] +" "+ horizontalStart[3]);
                            horizontalStart[0] = horizontalStart[1];
                            horizontalStart[1] = horizontalStart[2];
                            horizontalStart[2] = horizontalStart[3];
                            horizontalStart[3] = 10;
                            System.out.println("Przemieściłem horizontalStart_[" + horizontalStart[0] +" "+ horizontalStart[1]+" "+ horizontalStart[2] +" "+ horizontalStart[3]);
                        }
                    }
                    Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() <= 0){
                        Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());
                        Fleet[i].setDegree(0d);
                        if(Fleet[i].getY() == 110){
                            points = points +10;
                            System.out.println("Masz_" + points +"pkt.");
                        }
                    }
                }
            }else if(Fleet[i].getFly() && Fleet[i].getY() <= 110 && Fleet[i].getY() > 0 && Fleet[i].getX() <= 460 && Fleet[i].getX() >= 350 && Fleet[i].getDegree() <= 90){
                Fleet[i].setDegree(Fleet[i].getDegree() + (Fleet[i].getSpeed()));
                radDegree = (Math.PI * Fleet[i].getDegree())/180;
                Fleet[i].setX((int) (460d - radius * Math.cos(radDegree)));
                Fleet[i].setY((int) (110d - radius * Math.sin(radDegree)));
            }else if(Fleet[i].getFly() && Fleet[i].getY() < 20 && Fleet[i].getX() >= 460 && Fleet[i].getX() <= 840){
                 Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                 if(Fleet[i].getX() == 840){
                     newPlane(i);
                 }
            }  
        }
        private void changeVertical     (int i){
            if(Fleet[i].getY() >= 600 && Fleet[i].getX() >= 700 && !Fleet[i].getStart()){
                Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());
                Fleet[i].setXChange(700);
                Fleet[i].setYChange(600);
            }else if(Fleet[i].getX() > 600 && !Fleet[i].getStart()){ //dodać warunek na pozycje Y
                Fleet[i].setDegree(Fleet[i].getDegree() - (Fleet[i].getSpeed()));
                radDegree = (Math.PI * Fleet[i].getDegree())/180;
                Fleet[i].setX((int) (650d + radius2 * Math.cos(radDegree)));
                Fleet[i].setY((int) (Fleet[i].getYChange() + radius2 * Math.sin(radDegree)));
            }else if(Fleet[i].getX() == 600 && Fleet[i].getY() <= 650 && !Fleet[i].getStart()){
                if(Fleet[i].getX() == 600 ){
                    Fleet[i].setDegree(-180d);
                }
                Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
            }else if(Fleet[i].getY() >= 650 && Fleet[i].getX() <= 600 && Fleet[i].getX() > 500 && Fleet[i].getDegree() < -90 && !Fleet[i].getStart()){
                Fleet[i].setDegree(Fleet[i].getDegree() + (Fleet[i].getSpeed()));
                radDegree = (Math.PI * Fleet[i].getDegree())/180;
                Fleet[i].setX((int) (550d - radius2 * Math.cos(radDegree)));
                Fleet[i].setY((int) (650d - radius2 * Math.sin(radDegree)));
            }else if(Fleet[i].getY() >= 650 && Fleet[i].getDegree() == -90 && !Fleet[i].getStart()){
                Fleet[i].setY(700);
                Fleet[i].setX(550);
                Fleet[i].setLaneChange(false);
                Fleet[i].setLane(false);
                Fleet[i].setDegree(0);
            }
        }
        
        private void parkingVertical    (int i){
            for(int j = 0; j < 3; j++){
                if(horizontalParking[j] == 10 && !Fleet[i].getParked()){
                    horizontalParking[j] = i;
                    Fleet[i].setParked(true);
                    System.out.println("horizontalParking_" + j + " sam nr:" + i);
                }
            }    
            if( i == horizontalParking[0]){
                if(Fleet[i].getY() > 250){
                    Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());
                }else{
                    if(Fleet[i].getX() > 100 && Fleet[i].getDegree() >= -90){
                        Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                        if(Fleet[i].getDegree() <= -90){
                            Fleet[i].setDegree(-90d);
                            Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());                                    
                        }                                        
                    }else if(Fleet[i].getDegree() <= 0){
                        Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                        if(Fleet[i].getDegree() >= 0){
                            Fleet[i].setDegree(0d);
                            Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());                                           
                        }
                    }
                }
            }
            if( i == horizontalParking[1]){
                if(Fleet[i].getY() > 250){
                    Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());
                }else{
                    if(Fleet[i].getX() <= 140 && Fleet[i].getDegree() <= 90){
                       Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                        if(Fleet[i].getDegree() >= 90){
                            Fleet[i].setDegree(90d);
                            Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                        }
                    }else if(Fleet[i].getDegree() >= 0){
                        Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                        if(Fleet[i].getDegree() <= 0){
                            Fleet[i].setDegree(0d);
                            Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed()); 
                        }
                    }
                }
            }
            if( i == horizontalParking[2]){
                if(Fleet[i].getY() > 250){
                    Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());
                }else{
                    if(Fleet[i].getX() <= 180 && Fleet[i].getDegree() <= 90){
                           Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                           if(Fleet[i].getDegree() >= 90){
                                Fleet[i].setDegree(90d);
                                Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());  
                           }
                    }else if(Fleet[i].getDegree() >= 0){
                        Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                        if(Fleet[i].getDegree() <= 0){
                            Fleet[i].setDegree(0d);
                            Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());  
                        }
                    }                    
                }
            }
        }
        private void crossingVertical   (int i){
            if(Fleet[i].getCrossing()){
                if(horizontal[0] == 10 && horizontal[1] != 10){
                     System.out.println("Przemieszczam horizontal_[" + horizontal[0] +" "+ horizontal[1]+" "+ horizontal[2]);
                    horizontal[0] = horizontal[1];
                    horizontal[1] = horizontal[2];
                    horizontal[2] = 10;
                    System.out.println("Przemieściłem horizontal_[" + horizontal[0] +" "+ horizontal[1]+" "+ horizontal[2]);
                }
            }
            for(int j = 0; j < 3; j++){
                if(horizontal[j] == 10 && !Fleet[i].getCrossing()){
                    horizontal[j] = i;
                    Fleet[i].setCrossing(true);
                    System.out.println("Horizontal_" + j + " sam nr:" + i);
                }
            }
            if(horizontal[0] == i){
                if(Fleet[i].getY() <= 260 && Fleet[i].getX() < 195){
                    Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                }else if(Fleet[i].getX() < 195 && Fleet[i].getY() <= 320 && Fleet[i].getDegree() <= 90){
                    Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() >= 90){
                        Fleet[i].setDegree(90d);
                        Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                    }
                }else if(Fleet[i].getY() <= 320 && Fleet[i].getDegree() <= 180){
                    Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() >= 180){
                        Fleet[i].setDegree(180d);
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                    }
                    if(Fleet[i].getParked()){
                        Fleet[i].setParked(false);
                        for(int j = 0; j < 3; j++){
                            if(horizontalParking[j] == i){
                                horizontalParking[j] = 10;
                                System.out.println("Opusciłem horizontalParking_" + j + " sam nr:" + i);
                            }
                        }
                    }
                }

                if(Fleet[i].getX() > 195 && Fleet[i].getDegree() <= 270){
                    Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() >= 270){
                        Fleet[i].setDegree(270d);
                        Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());
                    }
                }
                else if(Fleet[i].getX() >= 195 && Fleet[i].getY() <= 320 && Fleet[i].getDegree() >= 180){
                    Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() <= 180){
                        Fleet[i].setDegree(180d);
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                    }
                }
            }
            if(horizontal[1] == i){
                if(Fleet[i].getY() <= 260){
                    Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                }else if(Fleet[i].getX() <= 230 && Fleet[i].getDegree() <= 90){
                    Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() >= 90){
                        Fleet[i].setDegree(90d);
                        Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                    }
                }else if(Fleet[i].getX() >= 230 && Fleet[i].getY() <= 295 && Fleet[i].getDegree() <= 180 ){
                    Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() >= 180){
                        Fleet[i].setDegree(180d);
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                    }
                    if(Fleet[i].getParked()){
                        Fleet[i].setParked(false);
                        for(int j = 0; j < 3; j++){
                            if(horizontalParking[j] == i){
                                horizontalParking[j] = 10;
                                System.out.println("Opusciłem horizontalParking_" + j + " sam nr:" + i);
                            }
                        }
                    }
                }
                if(Fleet[i].getX() > 231 || Fleet[i].getY() > 295){
                    if(Fleet[i].getX() > 231 && Fleet[i].getDegree() <= 270){
                        Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                        if(Fleet[i].getDegree() >= 270){
                            Fleet[i].setDegree(270d);
                            Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());
                        }
                    }
                }
            }
            if(horizontal[2] == i){
                if(Fleet[i].getY() <= 260){
                    Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                }else if(Fleet[i].getX() <= 265 && Fleet[i].getDegree() <= 90){
                    Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() >= 90){
                        Fleet[i].setDegree(90d);
                        Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                    }
                }else if(Fleet[i].getX() >= 265 && Fleet[i].getY() <= 295 && Fleet[i].getDegree() <= 180){
                    Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() >= 180){
                        Fleet[i].setDegree(180d);
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                    }
                    if(Fleet[i].getParked()){
                        Fleet[i].setParked(false);
                        for(int j = 0; j < 3; j++){
                            if(horizontalParking[j] == i){
                                horizontalParking[j] = 10;
                                System.out.println("Opusciłem horizontalParking_" + j + " sam nr:" + i);
                            }
                        }
                    }
                }
            }
        }
        private void takingOffVertical  (int i){
            for(int j = 0; j < 3; j++){
                    if(horizontalStart[j] == 10 && !Fleet[i].getTakingOff()){
                        horizontalStart[j] = i;
                        Fleet[i].setTakingOff(true);
                        System.out.println("HorizontalStart_" + j + " sam nr:" + i);
                    }
                }
                
                if(horizontalStart[0] == i){
                    if(Fleet[i].getY() <= 565){
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                        if(Fleet[i].getY() <= 410){
                            if(Fleet[i].getCrossing()){
                                Fleet[i].setCrossing(false);
                                for(int j = 0; j < 3; j++){
                                    if(horizontal[j] == i){
                                        horizontal[j] = 10;
                                        System.out.println("Opusciłem horizontal_" + j + " sam nr:" + i);
                                    }
                                }
                            }
                        }
                    }
                    if(Fleet[i].getX() <= 315 && Fleet[i].getY() >= 565 && Fleet[i].getDegree() >= 90){
                        Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                        if(Fleet[i].getDegree() <= 90){
                            Fleet[i].setDegree(90d);
                        Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());                   
                        }
                    }
                }
                if(horizontalStart[1] == i){
                    if(Fleet[i].getY() <= 565){
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                        if(Fleet[i].getY() <= 410){
                            if(Fleet[i].getCrossing()){
                                Fleet[i].setCrossing(false);
                                for(int j = 0; j < 3; j++){
                                    if(horizontal[j] == i){
                                        horizontal[j] = 10;
                                        System.out.println("Opusciłem horizontal_" + j + " sam nr:" + i);
                                    }
                                }
                            }
                        }
                    }else if(Fleet[i].getX() <= 280 && Fleet[i].getDegree() >= 90){
                        Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                        if(Fleet[i].getDegree() <= 90){
                            Fleet[i].setDegree(90d);
                            Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                        }

                    }
                }
                if(horizontalStart[2] == i){
                    if(Fleet[i].getY() <= 565){
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                        if(Fleet[i].getY() <= 410){
                            if(Fleet[i].getCrossing()){
                                Fleet[i].setCrossing(false);
                                for(int j = 0; j < 3; j++){
                                    if(horizontal[j] == i){
                                        horizontal[j] = 10;
                                        System.out.println("Opusciłem horizontal_" + j + " sam nr:" + i);
                                    }
                                }
                            }
                        }
                    }else if(Fleet[i].getX() <= 245 && Fleet[i].getDegree() >= 90){
                        Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                        if(Fleet[i].getDegree() <= 90){
                            Fleet[i].setDegree(90d);
                        Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                        }
                    }
                }
                if(horizontalStart[3] == i){
                    if(Fleet[i].getY() <= 565){
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                        if(Fleet[i].getY() <= 410){
                            if(Fleet[i].getCrossing()){
                                Fleet[i].setCrossing(false);
                                for(int j = 0; j < 3; j++){
                                    if(horizontal[j] == i){
                                        horizontal[j] = 10;
                                        System.out.println("Opusciłem horizontal_" + j + " sam nr:" + i);
                                    }
                                }
                            }  
                        }
                    }else if(Fleet[i].getX() <= 210 && Fleet[i].getDegree() >= 90){
                        Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                        if(Fleet[i].getDegree() <= 90){
                            Fleet[i].setDegree(90d);
                            Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                        }
                    }
                }                
        }
//przylot poziomy z prawej
        private void comingHorizontal   (int i){
            if(Fleet[i].getX() >= 460 && Fleet[i].getY() >= 600){
                Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());                                
            }else if(Fleet[i].getY() >= 600 && Fleet[i].getX() <= 460 && Fleet[i].getX() >= 360 && Fleet[i].getDegree() < 90 && !Fleet[i].getStart()){
                Fleet[i].setDegree(Fleet[i].getDegree() + (Fleet[i].getSpeed()));
                radDegree = (Math.PI * Fleet[i].getDegree())/180;
                Fleet[i].setX((int) (460d - radius * Math.sin(radDegree)));
                Fleet[i].setY((int) (600d + radius * Math.cos(radDegree)));
            }else if(Fleet[i].getY() <= 600 && Fleet[i].getY() > 115 && Fleet[i].getX() <= 360 && Fleet[i].getX() >= 320 && !Fleet[i].getStart()){
                Fleet[i].changePosition(true); //wylądowałem
                Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());
            }else if(Fleet[i].getY() <= 115 && Fleet[i].getY() >= 100 && Fleet[i].getX() <= 360 && Fleet[i].getX() >= 320 && Fleet[i].getDegree() >= 0 && !Fleet[i].getStart()){
                Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                if(Fleet[i].getDegree() <= 0){
                    Fleet[i].setDegree(0d);
                    Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());                                    
                }
            }else if(Fleet[i].getX() <= 320 && Fleet[i].getX() >= 210 &&  Fleet[i].getY() >= 60 &&  Fleet[i].getY() <= 190 && !Fleet[i].getStart()){ //PARKOWANIE 
                parkingHorizontal(i);
            }else if(Fleet[i].getX() <= 220 && Fleet[i].getX() >= 200 && Fleet[i].getLoad() < 500 && Fleet[i].getParked() && !Fleet[i].getStart()){ //Wsiadanie pasażerów
                Fleet[i].setLoad(Fleet[i].getLoad() + Fleet[i].getSpeed());
            }else if(Fleet[i].getY() <= 320 && Fleet[i].getY() >= 100 && Fleet[i].getX() <= 350 && Fleet[i].getX() >= 190 && Fleet[i].getStart() && Fleet[i].getLoad() >= 500 && !Fleet[i].getCross()){
                crossingHorizontal(i);
            }else if(Fleet[i].getCross() && Fleet[i].getStart() && !Fleet[i].getFly()){//kolejna faza 
                takingOffHorizontal(i);
            }else if(Fleet[i].getFly() && Fleet[i].getX() < 600 && Fleet[i].getX() > 110 && Fleet[i].getY() <= 360 && Fleet[i].getY() >= 310){
                if(Fleet[i].getY() < 360){
                    Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                }else{
                    if(Fleet[i].getTakingOff()){
                        Fleet[i].setTakingOff(false);
                        for(int j = 0; j < 3; j++){
                            if(verticalStart[j] == i){
                                verticalStart[j] = 10;
                                System.out.println("Opusciłem verticalStart_" + j + " sam nr:" + i);
                            }
                        }
                        if(verticalStart[0] == 10 && verticalStart[1] != 10){
                            System.out.println("Przemieszczam verticalStart_[" + verticalStart[0] +" "+ verticalStart[1]+" "+ verticalStart[2] +" "+ verticalStart[3]);
                            verticalStart[0] = verticalStart[1];
                            verticalStart[1] = verticalStart[2];
                            verticalStart[2] = verticalStart[3];
                            verticalStart[3] = 10;
                            System.out.println("Przemieściłem verticalStart_[" + verticalStart[0] +" "+ verticalStart[1]+" "+ verticalStart[2] +" "+ verticalStart[3]);
                        }
                    }
                    Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() >= 0){
                        Fleet[i].setDegree(0d);
                        Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());
                        if(Fleet[i].getX() == 110){
                            points = points + 10;
                            System.out.println("Masz_" + points +"pkt.");
                        }
                    }
                }
            }else if(Fleet[i].getFly() && Fleet[i].getX() <= 110 && Fleet[i].getX() > 0 && Fleet[i].getY() <= 460 && Fleet[i].getY() >= 350 && Fleet[i].getDegree() >= -90){
                Fleet[i].setDegree(Fleet[i].getDegree() - (Fleet[i].getSpeed()));
                radDegree = (Math.PI * Fleet[i].getDegree())/180;
                Fleet[i].setX((int) (110d + radius * Math.sin(radDegree)));
                Fleet[i].setY((int) (460d - radius * Math.cos(radDegree)));
            }else if(Fleet[i].getFly() && Fleet[i].getX() < 20 && Fleet[i].getY() >= 460 && Fleet[i].getY() <= 840){
                 Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                 if(Fleet[i].getY() == 840){
                    newPlane(i);
                 }
            }    
        }
        private void changeHorizontal   (int i){
             if(Fleet[i].getX() >= 600 && Fleet[i].getY() >= 700){
                Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());
                Fleet[i].setXChange(600);
                Fleet[i].setYChange(700);
            }else if(Fleet[i].getY() > 600){ 
                Fleet[i].setDegree(Fleet[i].getDegree() + (Fleet[i].getSpeed()));
                radDegree = (Math.PI * Fleet[i].getDegree())/180;
                Fleet[i].setX((int) (Fleet[i].getXChange() - radius2 * Math.sin(radDegree)));
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
            }else if(Fleet[i].getX() >= 650 && Fleet[i].getDegree() == -270){
                Fleet[i].setY(550);
                Fleet[i].setX(700);
                Fleet[i].setLaneChange(false);
                Fleet[i].setLane(true);
                Fleet[i].setDegree(0);
            }
        }
        
        private void parkingHorizontal  (int i){
            for(int j = 0; j < 3; j++){
                if(verticalParking[j] == 10 && !Fleet[i].getParked()){
                    verticalParking[j] = i;
                    Fleet[i].setParked(true);
                    System.out.println("verticalParking_" + j + " sam nr:" + i);
                }
            }    
            if(i == verticalParking[0]){
                if(Fleet[i].getX() > 250){
                    Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());
                }else{
                    if(Fleet[i].getY() > 100 && Fleet[i].getDegree() <= 90){
                        Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                        if(Fleet[i].getDegree() >= 90){
                            Fleet[i].setDegree(90d);
                            Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());
                        }                                            
                    }else if(Fleet[i].getDegree() >= 0){
                        Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                        if(Fleet[i].getDegree() <= 0){
                            Fleet[i].setDegree(0d);
                            Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());   
                        }
                    }
                }
            }
            if( i == verticalParking[1]){
                if(Fleet[i].getX() > 250){
                    Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());
                }else{
                    if(Fleet[i].getY() <= 140 && Fleet[i].getDegree() >= -90){
                        Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                        if(Fleet[i].getDegree() <= -90){
                            Fleet[i].setDegree(-90d);
                            Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());  
                        }
                    }else if(Fleet[i].getDegree() <= 0){
                        Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                        if(Fleet[i].getDegree() >= 0){
                            Fleet[i].setDegree(0d);
                            Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());   
                        }
                    }
                }
            }
            if( i == verticalParking[2]){
                if(Fleet[i].getX() > 250){
                    Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());
                }else{
                    if(Fleet[i].getY() <= 180 && Fleet[i].getDegree() >= -90){
                        Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                        if(Fleet[i].getDegree() <= -90){
                            Fleet[i].setDegree(-90d);
                            Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                        }
                    }else if(Fleet[i].getDegree() <= 0){
                        Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                        if(Fleet[i].getDegree() >= 0){
                            Fleet[i].setDegree(0d);
                            Fleet[i].setX(Fleet[i].getX() - Fleet[i].getSpeed());  
                        }
                    }
                }
            }
        }
        private void crossingHorizontal (int i){
            if(Fleet[i].getCrossing()){
                if(vertical[0] == 10 && vertical[1] != 10){
                     System.out.println("Przemieszczam vertical_[" + vertical[0] +" "+ vertical[1]+" "+ vertical[2]);
                    vertical[0] = vertical[1];
                    vertical[1] = vertical[2];
                    vertical[2] = 10;
                    System.out.println("Przemieściłem vertical_[" + vertical[0] +" "+ vertical[1]+" "+ vertical[2]);
                }
            }
            for(int j = 0; j < 3; j++){
                if(vertical[j] == 10 && !Fleet[i].getCrossing()){
                    vertical[j] = i;
                    Fleet[i].setCrossing(true);
                    System.out.println("vertical_" + j + " sam nr:" + i);
                }
            }
            if(vertical[0] == i){
                if(Fleet[i].getX() <= 260 && Fleet[i].getY() < 195){
                    Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                }else if(Fleet[i].getY() < 195 && Fleet[i].getX() <= 320 && Fleet[i].getDegree() >= -90){
                    Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() <= -90){
                        Fleet[i].setDegree(-90d);
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                    }
                }else if(Fleet[i].getX() <= 320 && Fleet[i].getDegree() >= -180){
                    Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() <= -180){
                        Fleet[i].setDegree(-180d);
                        Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                    }
                    if(Fleet[i].getParked()){
                        Fleet[i].setParked(false);
                        for(int j = 0; j < 3; j++){
                            if(verticalParking[j] == i){
                                verticalParking[j] = 10;
                                System.out.println("Opusciłem verticalParking_" + j + " sam nr:" + i);
                            }
                        }
                    }
                }
                if(Fleet[i].getY() > 195 && Fleet[i].getDegree() >= -270){
                    Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() <= -270){
                        Fleet[i].setDegree(-270d);
                        Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());
                    }
                }
                if(Fleet[i].getY() <= 195 && Fleet[i].getX() <= 320 && Fleet[i].getDegree() <= -180 ){
                    Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() >= -180){
                        Fleet[i].setDegree(-180d);
                        Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                    }
                }
            }

            if(vertical[1] == i){
                if(Fleet[i].getX() <= 260){
                    Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                }else if(Fleet[i].getY() <= 230 && Fleet[i].getDegree() >= -90){
                    Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() <= -90){
                        Fleet[i].setDegree(-90d);
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                    }
                }else if(Fleet[i].getY() >= 230 && Fleet[i].getX() <= 295 && Fleet[i].getDegree() >= -180){
                    Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() <= -180){
                        Fleet[i].setDegree(-180d);
                        Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                    }
                    if(Fleet[i].getParked()){
                        Fleet[i].setParked(false);
                        for(int j = 0; j < 3; j++){
                            if(verticalParking[j] == i){
                                verticalParking[j] = 10;
                                System.out.println("Opusciłem verticalParking_" + j + " sam nr:" + i);
                            }
                        }
                    }
                }
                if(Fleet[i].getY() > 231 || Fleet[i].getX() > 295){
                    if(Fleet[i].getY() > 231 && Fleet[i].getDegree() >= -270){
                        Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                        if(Fleet[i].getDegree() <= -270){
                            Fleet[i].setDegree(-270d);
                            Fleet[i].setY(Fleet[i].getY() - Fleet[i].getSpeed());
                        }
                    }
                }
            }
            if(vertical[2] == i){
                if(Fleet[i].getX() <= 260){
                    Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                }else if(Fleet[i].getY() <= 265 && Fleet[i].getDegree() >= -90){
                    Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() <= -90){
                        Fleet[i].setDegree(-90d);
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                    }
                }else if(Fleet[i].getY() >= 265 && Fleet[i].getX() <= 295 && Fleet[i].getDegree() >= -180){
                    Fleet[i].setDegree(Fleet[i].getDegree() - Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() <= -180){
                        Fleet[i].setDegree(-180d);
                        Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                    }
                    if(Fleet[i].getParked()){
                        Fleet[i].setParked(false);
                        for(int j = 0; j < 3; j++){
                            if(verticalParking[j] == i){
                                verticalParking[j] = 10;
                                System.out.println("Opusciłem verticalParking_" + j + " sam nr:" + i);
                            }
                        }
                    }
                }
            }
        }
        private void takingOffHorizontal(int i){
            for(int j = 0; j < 3; j++){
                if(verticalStart[j] == 10 && !Fleet[i].getTakingOff()){
                    verticalStart[j] = i;
                    Fleet[i].setTakingOff(true);
                    System.out.println("verticalStart_" + j + " sam nr:" + i);
                }
            }

            if(verticalStart[0] == i){
                if(Fleet[i].getX() <= 565){
                    Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                    if(Fleet[i].getX() <= 410){
                        if(Fleet[i].getCrossing()){
                            Fleet[i].setCrossing(false);
                            for(int j = 0; j < 3; j++){
                                if(vertical[j] == i){
                                    vertical[j] = 10;
                                    System.out.println("Opusciłem vertical_" + j + " sam nr:" + i);
                                }
                            }
                        }
                    }
                }
                if(Fleet[i].getY() <= 315 && Fleet[i].getX() >= 565 && Fleet[i].getDegree() <= -90){
                    Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() >= -90){
                        Fleet[i].setDegree(-90d);
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                    }
                }
            }
            if(verticalStart[1] == i){
                if(Fleet[i].getX() <= 565){
                    Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                    if(Fleet[i].getX() <= 410){
                        if(Fleet[i].getCrossing()){
                            Fleet[i].setCrossing(false);
                            for(int j = 0; j < 3; j++){
                                if(vertical[j] == i){
                                    vertical[j] = 10;
                                    System.out.println("Opusciłem vertical_" + j + " sam nr:" + i);
                                }
                            }
                        }
                    }
                }else if(Fleet[i].getY() <= 280 && Fleet[i].getDegree() <= -90){
                    Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() >= -90){
                        Fleet[i].setDegree(-90d);
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                    }
                }
            }
            if(verticalStart[2] == i){
                if(Fleet[i].getX() <= 565){
                    Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                    if(Fleet[i].getX() <= 410){
                        if(Fleet[i].getCrossing()){
                            Fleet[i].setCrossing(false);
                            for(int j = 0; j < 3; j++){
                                if(vertical[j] == i){
                                    vertical[j] = 10;
                                    System.out.println("Opusciłem vertical_" + j + " sam nr:" + i);
                                }
                            }
                        }
                    }
                }else if(Fleet[i].getY() <= 245 && Fleet[i].getDegree() <= -90){
                    Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() >= -90){
                        Fleet[i].setDegree(-90d);
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                    }
                }
            }
            if(verticalStart[3] == i){
                if(Fleet[i].getX() <= 565){
                    Fleet[i].setX(Fleet[i].getX() + Fleet[i].getSpeed());
                    if(Fleet[i].getX() <= 410){
                        if(Fleet[i].getCrossing()){
                            Fleet[i].setCrossing(false);
                            for(int j = 0; j < 3; j++){
                                if(vertical[j] == i){
                                    vertical[j] = 10;
                                    System.out.println("Opusciłem vertical_" + j + " sam nr:" + i);
                                }
                            }
                        }
                    }
                }else if(Fleet[i].getY() <= 210&& Fleet[i].getDegree() <= -90){
                    Fleet[i].setDegree(Fleet[i].getDegree() + Fleet[i].getSpeed());
                    if(Fleet[i].getDegree() >= -90){
                        Fleet[i].setDegree(-90d);
                        Fleet[i].setY(Fleet[i].getY() + Fleet[i].getSpeed());
                    }
                }
            }      
        }
        
        private void detectionCollision (){
            for(int j = 0; j < Fleet.length; j++){ //KOLIZJE 
                for(int k = 0; k < Fleet.length; k++){
                    if(Fleet[j].getPosition() && Fleet[k].getPosition() && !Fleet[k].getCollision() && j != k){
                        if(Fleet[j].getX() >= Fleet[k].getX() && Fleet[j].getX() <= (Fleet[k].getX() + 30) && Fleet[j].getY() >= Fleet[k].getY() && Fleet[j].getY() <= (Fleet[k].getY() + 30 )){
                            Fleet[j].setCollision(true);
                            Fleet[k].setCollision(true);
                            System.out.println("Nr " + j + " zderzenie z nr " + k);
                            System.out.println("[" +Fleet[j].getX() + "," + Fleet[j].getY() + "]   [" +Fleet[k].getX() + "," + Fleet[k].getY() + "]" );
                            if(timetrial){
                                gameover = true;
                                start = false;
                                endTime = System.currentTimeMillis();
                                newFleet();
                                newParking();
                            }else{
                                points = points -20;
                                for(int z = 0; z < 3; z++){
                                    if(horizontalParking[z] == k || horizontalParking[z] == j){
                                        horizontalParking[z] = 10;
                                    }
                                    if(horizontal[z] == k || horizontal[z] == j){
                                        horizontal[z] = 10;
                                    }
                                    if(horizontalStart[z] == k || horizontalStart[z] == j){
                                        horizontalStart[z] = 10;
                                    }
                                    if(verticalParking[z] == k || verticalParking[z] == j){
                                        verticalParking[z] = 10;
                                    }
                                    if(vertical[z] == k || vertical[z] == j){
                                        vertical[z] = 10;
                                    }
                                    if(verticalStart[z] == k || verticalStart[z] == j){
                                        verticalStart[z] = 10;
                                    }                                    
                                }
                                newPlane(j);
                                newPlane(k);
                            }
                        }
                    }
                }
            }
        }
        public  void newPlane           (int i){
            boolean iLane = Math.random() < 0.5;
                Random rand = new Random();            
                int iRand = 50 * (rand.nextInt(5)+1);
                if(iLane){
                    Fleet[i] = new Plane(700, 800 + i * iRand, iLane);
                }else{
                    Fleet[i] = new Plane(800 + i * iRand, 700, iLane);
                }
            System.out.println("Samolot nr" + i + " [" + Fleet[i].getX() + "," + Fleet[i].getY() + "] " + Fleet[i].getLane());
        }
    }
    
    public class Plane{
        private final int   wsp[] = new int[2];
        private double      degree;     //kąt skretu        
        private boolean     lane;       //trasa wlotu
        private int         speed;  //prędkość
        private boolean     collision;  //czy się zderzył
        private boolean     laneChange;         //zmiana trasy
        private int         xChange, yChange;   //dane do skrętu
        private boolean     position;   //czy wylądował
        private boolean     parked, crossing, takingOff;     //czy zaparkował
        private int         load;               //wsiadanie pasażerów
        private boolean     go, cross, fly;  //odlot 

        public Plane(int x, int y, boolean iLane){
            wsp[0] =    x;
            wsp[1] =    y;
            lane =      iLane;      //pas na którym się znajduje
            degree =    0;          //do zmian pasa i ladowania
            speed = 1;
            laneChange = false;
            xChange = 0;            //współrzędne do zawracania
            yChange = 0;            //współrzędne do zawracania
            position =  false;      //czy wylądował
            collision = false;      //czy zderzenie
            parked = false; crossing = false; takingOff = false; load = 0;
            go = false;  cross = false; fly = false;
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
        public int      getXChange (){
            return xChange;
        }        
        public int      getYChange (){
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
        public boolean  getLaneChange(){
            return laneChange;
        }
        public boolean  getParked   (){
            return parked;
        }
        public boolean  getCrossing   (){
            return crossing;
        }
        public boolean  getTakingOff   (){
            return takingOff;
        }
        public int      getLoad     (){
            return load;
        }
        public boolean  getStart    (){
            return go;
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
        public void     setXChange  (int ixChange){
            xChange = ixChange;
        }        
        public void     setYChange  (int iyChange){
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
        public void     setLaneChange(boolean ilaneChange){
            laneChange = ilaneChange;
        }
        public void     setParked   (boolean iParked){
            parked = iParked;
        }
        public void     setCrossing   (boolean iParked){
            crossing = iParked;
        }
        public void     setTakingOff   (boolean iParked){
            takingOff = iParked;
        }
        public void     setLoad     (int iLoad){
            load = iLoad;
        }
        public void     setGo    (boolean iStart){
            go = iStart;
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
        newFleet();
        newParking();

        timer = new Timer();
        timer.scheduleAtFixedRate(new Flight(), 0, 10);  
    }
    public void newFleet(){
        Fleet = new Plane[10];
        for(int i = 0; i < Fleet.length; i++){
            boolean iLane = Math.random() < 0.5;
            Random rand = new Random();            
            int iRand = 100 + 50 * (rand.nextInt(10)+1);
            if(iLane){
                Fleet[i] = new Plane(700, 800 + i * iRand, iLane);
            }else{
                Fleet[i] = new Plane(800 + i * iRand, 700, iLane);
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
        horizontalStart = new int[4];
            for(int i = 0; i < horizontalStart.length; i++){
                horizontalStart[i] = 10;
            }
        verticalStart   = new int[4];
            for(int i = 0; i < verticalStart.length; i++){
                verticalStart[i] = 10;
            }
    }
    public void loadImages(){
        Menu =      new ImageIcon("Obrazki/menu.png").getImage();
        GameOver =  new ImageIcon("Obrazki/gameover.png").getImage();
        Win =       new ImageIcon("Obrazki/win.png").getImage();
        Lotnisko =  new ImageIcon("Obrazki/lotnisko.png").getImage();
        plane1 =  LoadImage("Obrazki/samolot1.png");
        plane2 =  LoadImage("Obrazki/samolot2.png");
    }
    private BufferedImage LoadImage(String ImageName){
        BufferedImage img = null;
        try{
             img = ImageIO.read(new File(ImageName));
        }catch(IOException e){  
        }
        return img;
      }		      
    @Override
    public void repaint(){
        BufferStrategy bstrategy = this.getBufferStrategy();
        Graphics2D g2D = (Graphics2D) bstrategy.getDrawGraphics();
        
        if(!start){ //Rysowanie menu
            if(!gameover){
                g2D.drawImage(Menu, 0,  26, null);      // Obcina 26px z góry.
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(e.getX() > 590 && e.getX() < 770 && e.getY() < 620 && e.getY() > 550 && !start){
                            start = true; gameover = false; points = 0; 
                            timetrial = true;
                            startTime = System.currentTimeMillis();                            
                        }
                        if(e.getX() > 590 && e.getX() < 770 && e.getY() < 720 && e.getY() > 650 && !start){
                            start = true; gameover = false; points = 0; 
                            timetrial = false;
                        }
                    }
                });
            }else{  //Game Over
                if(timetrial){
                    if((endTime - startTime) <= 10*60000){ //10 min
                        g2D.drawImage(GameOver, 0,  26, null);  // Obcina 26px z góry.
                    }else{
                        g2D.drawImage(Win, 0,  26, null);  // Obcina 26px z góry.
                    }
                    Color pedzel1 = new Color(0,0,0); 
                    g2D.setColor(pedzel1);
                    g2D.setFont(new Font("TimesRoman", Font.BOLD, 30));
                    g2D.drawString("Time: " + (endTime - startTime)/60000 + "min " + ((endTime - startTime)/1000)%60 + "s", 320, 310 + 26);
                    g2D.drawString("Your points: " + Integer.toString(points), 320, 350 + 26);
                }
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(e.getX() > 590 && e.getX() < 770 && e.getY() < 620 && e.getY() > 550 && !start){
                            start = true; gameover = false; points = 0; 
                            timetrial = true;
                            startTime = System.currentTimeMillis();                            
                        }
                        if(e.getX() > 590 && e.getX() < 770 && e.getY() < 720 && e.getY() > 650 && !start){
                            start = true; gameover = false; points = 0; 
                            timetrial = false;                         
                        }
                    }
                });
            }
        }else{      //Start gry
            g2D.drawImage(Lotnisko, 0,  26, null);      // Obcina 26px z góry.
            if(timetrial){
                Color bialy = new Color(255, 255, 255);
                g2D.setColor(bialy);
                g2D.fillRect(0, 26, 160, 30);
                Color pedzel = new Color(255,0,0); 
                g2D.setColor(pedzel);
                g2D.setFont(new Font("TimesRoman", Font.PLAIN, 20));
                endTime = System.currentTimeMillis();
                g2D.drawString("Time: " + (endTime - startTime)/60000 + "min " + ((endTime - startTime)/1000)%60 + "s", 20, 20 + 26);
            }
            if((endTime - startTime) >= 10*60000){
                gameover = true;
            }
            for(int i = 0; i < Fleet.length; i++ ){
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
                
                Color pedzel1 = new Color(255,255,255); 
                g2D.setColor(pedzel1);
                g2D.setFont(new Font("TimesRoman", Font.PLAIN, 20));
                g2D.drawString("Your Points: ", 67, 90 + 26);
                g2D.drawString(Integer.toString(points), 67, 120 + 26);
                if(Fleet[i].getX() <= 215 && Fleet[i].getY() <= 215 && Fleet[i].getParked()){
                    Color pedzel2 = new Color(255,255,255); g2D.setColor(pedzel2);
                    g2D.fillRect(Fleet[i].getX(), Fleet[i].getY() +20, 500/17, 5);
                    Color pedzel3 = new Color(255,0,0); g2D.setColor(pedzel3);
                    g2D.fillRect(Fleet[i].getX(), Fleet[i].getY() +20, Fleet[i].getLoad()/17, 5);
                }
            }
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {    
                   for(int i = 0; i < Fleet.length; i++){
                       int X = e.getX();
                       int Y = e.getY();
                       //zmian pasa
                        if((X -31) <= Fleet[i].getX() && X >= Fleet[i].getX() 
                                && (Y -30 -26) <= Fleet[i].getY() && Y >= (Fleet[i].getY() +26) 
                                && X >= 400 && Y >= 400){
                            if(!Fleet[i].getLaneChange()){    
                                Fleet[i].setLaneChange(!Fleet[i].getLaneChange());
                                Fleet[i].setXChange(Fleet[i].getX());
                                Fleet[i].setYChange(Fleet[i].getY());
                            }
                        }
                        //ustawianie w kolejkę do skrzyżowania
                        if((X -31) <= Fleet[i].getX() && X >= Fleet[i].getX() 
                                && (Y -30 -26) <= Fleet[i].getY() && Y >= (Fleet[i].getY() +26) 
                                && X <= 270 && Y <= (270 + 26) && Fleet[i].getLoad() >= 500){
                            if(!Fleet[i].getStart()){    
                                Fleet[i].setGo(!Fleet[i].getStart());
                                System.out.println("Start_" + i + "_" + Fleet[i].getStart() + "[" + Fleet[i].getX() +"," + Fleet[i].getY() + "]");
                            }
                        }
                        // cross poziom
                        if((X -31) <= Fleet[i].getX() && X >= Fleet[i].getX() 
                                && (Y -30 -26) <= Fleet[i].getY() && Y >= (Fleet[i].getY() +26) && X >= 190 && Y >= (310 + 26)
                                && X <= 230 && Y <= (350 + 26) && Fleet[i].getCrossing() && Fleet[i].getStart()){
                            if(!Fleet[i].getCross()){    
                                Fleet[i].setCross(true);
                                System.out.println("Cross_" + i + "_" + Fleet[i].getCross());
                            }
                        }
                        // cross pion
                        if((X -31) <= Fleet[i].getX() && X >= Fleet[i].getX() 
                                && (Y -30 -26) <= Fleet[i].getY() && Y >= (Fleet[i].getY() +26) && X >= 310 && Y >= (190 + 26)
                                && X <= 350 && Y <= (230 + 26) && Fleet[i].getCrossing() && Fleet[i].getStart()){
                            if(!Fleet[i].getCross()){    
                                Fleet[i].setCross(true);
                                System.out.println("Cross_" + i + "_" + Fleet[i].getCross());
                            }
                        }
                        // fly poziom
                        if((X -31) <= Fleet[i].getX() && X >= Fleet[i].getX() 
                                && (Y -30 -26) <= Fleet[i].getY() && Y >= (Fleet[i].getY() +26) && X >= 310 && Y >= (560 + 26)
                                && X <= 350 && Y <= (600 + 26) && Fleet[i].getStart()){
                            if(!Fleet[i].getFly()){    
                                Fleet[i].setFly(true);
                                System.out.println("Fly_" + i + "_" + Fleet[i].getFly() + "[" + Fleet[i].getX() + "," + Fleet[i].getY() + "]");
                            }
                        }
                        // fly pion
                        if((X -31) <= Fleet[i].getX() && X >= Fleet[i].getX() 
                                && (Y -30 -26) <= Fleet[i].getY() && Y >= (Fleet[i].getY() +26) && X >= 560 && Y >= (310 + 26)
                                && X <= 600 && Y <= (350 + 26) && Fleet[i].getStart()){
                            if(!Fleet[i].getFly()){    
                                Fleet[i].setFly(true);
                                System.out.println("Fly_ " + i + "_" + Fleet[i].getFly() + "[" + Fleet[i].getX() + "," + Fleet[i].getY() + "]");
                            }
                        }
                   }
                }
            });
        }
        g2D.dispose();
        bstrategy.show();
    }
}
