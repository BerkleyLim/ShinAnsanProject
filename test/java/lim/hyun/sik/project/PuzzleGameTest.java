package lim.hyun.sik.project;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import java.io.*;

import lim.hyun.sik.project.puzzle.*;
import lim.hyun.sik.project.chat.client.ClientMessage;
import lim.hyun.sik.project.chat.server.ServerMessage;

public class PuzzleGameTest extends JFrame implements ActionListener {
    JMenuBar mb=new JMenuBar();

    JMenu menu_game=new JMenu("게임");
    JMenu menu_level=new JMenu("난이도");
    JMenu menu_option=new JMenu("옵션");

    JMenuItem menu_game_new=new JMenuItem("새게임");
    JMenuItem menu_game_start=new JMenuItem("게임시작");
    JMenuItem menu_game_exit=new JMenuItem("게임종료");

    JMenuItem menu_level_3=new JMenuItem("3 X 3");
    JMenuItem menu_level_4=new JMenuItem("4 X 4");
    JMenuItem menu_level_5=new JMenuItem("5 X 5");



    //JMenuItem menu_option_score=new JMenuItem("점수목록");

    /*JMenuItem menu_option_full=new JMenuItem("완성그림 엿보기");
    JMenuItem menu_option_cho=new JMenuItem("게임 계속하기");*/

    JLabel scoreLabel = new JLabel("점수 : 1000000", JLabel.CENTER);
    JLabel countLabel = new JLabel("이동회수 : 0", JLabel.CENTER);

    JPanel panel = new JPanel();


    String[][] name=new String[7][10];
    int[][] jumsu=new int[7][10];

    GamePan gp=new GamePan(name, jumsu, scoreLabel, countLabel);
    private final JMenuItem mntmNewMenuItem = new JMenuItem("\uBB38\uC758\uC0AC\uD56D");

    public PuzzleGame() {
        super("키위 스무디의 작품");

        Container cp=getContentPane();

        menu_game.add(menu_game_new);
        menu_game.add(menu_game_start);
        menu_game.addSeparator();
        menu_game.add(menu_game_exit);

        menu_level.add(menu_level_3);
        menu_level.add(menu_level_4);
        menu_level.add(menu_level_5);

        //menu_option.add(menu_option_score);

        //여기는 꽉찬 그림 실시 menu_option.add(menu_option_full);

        mb.add(menu_game);
        mb.add(menu_level);
        mb.add(menu_option);
        menu_option.add(mntmNewMenuItem);

        setJMenuBar(mb);

        panel.setLayout(new GridLayout(1,2));

        panel.add(scoreLabel);
        panel.add(countLabel);
        cp.add(panel, BorderLayout.NORTH);
        cp.add(gp, BorderLayout.CENTER);

        for(int a=0; a<7; a++) {
            for(int b=0;b<10;b++) {
                name[a][b]="";
                jumsu[a][b]=0;
            }
        }
 
        setBounds(200,200, 500,500);

        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });	

        menu_game_new.addActionListener(this);
        menu_game_start.addActionListener(this);
        menu_game_exit.addActionListener(this);

        menu_level_3.addActionListener(this);
        menu_level_4.addActionListener(this);
        menu_level_5.addActionListener(this);

        //옵션 메뉴의 점수 기능 일단 제거(차후 채팅 연동)
        //menu_option_score.addActionListener(this);

        /* 옵션 이벤트 완성그림 확인 및 게임 계속하기
        menu_option_full.addActionListener(this);
        menu_option_cho.addActionListener(this);
        */
    }

    public void actionPerformed(ActionEvent e) {
        Object o=e.getSource();

        if(o==menu_game_new) {
            gp.bStart=false;
            gp.bFull=true;
            gp.score = 1000000;
            gp.count = 0;

            gp.count=0;

            gp.getRand();
            gp.getRect();
            gp.repaint();
            gp.scoreLabel.setText("점수 : " + gp.score);
            gp.countLabel.setText("이동회수 : " + gp.count);

        } else if(o==menu_game_start) {
            gp.bStart=true;
            gp.bFull=false;
            gp.repaint();
        } else if(o==menu_game_exit) {
            System.exit(0);
        } else if(o==menu_level_3) {
            gp.chasu=3;
            gp.getRand();
            gp.getRect();
            gp.repaint();
        } else if(o==menu_level_4) {
            gp.chasu=4;
            gp.getRand();
            gp.getRect();
            gp.repaint();
        } else if(o==menu_level_5) {
            gp.chasu=5;
            gp.getRand();
            gp.getRect();
            gp.repaint();
        }

        /* 점수 기능 제거
        else if(o==menu_option_score) {
            ScoreList sl=new ScoreList(name, jumsu, gp.chasu-3);
            sl.setVisible(true);
        }
        */


        /* 임시 제거
        else if(o==menu_option_full || o==menu_option_cho) {
            if(!gp.bStart) return;

            if(gp.bFull) {
                menu_option_full.setText("완성그림 엿보기");
                //menu_option.remove(1);
                //menu_option.insert(menu_option_full,1);
            } else {
                menu_option_full.setText("게임 계속하기");
                //menu_option.remove(1);
                //menu_option.insert(menu_option_cho,1);
            }

            gp.bFull=!gp.bFull;
            gp.repaint();
        }*/
    }

    public static void main(String[] args) {
        new PuzzleGame();
    }
}
