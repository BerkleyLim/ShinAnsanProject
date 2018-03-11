package lim.hyun.sik.project.puzzle;

class GamePan extends JPanel {
    Image img = Toolkit.getDefaultToolkit().getImage("솔지.jpg");
    
    MediaTracker mt = new MediaTracker(this);
    
    JLabel scoreLabel = new JLabel("");
    JLabel countLabel = new JLabel("");
    
    int chasu;
    int width, high;
    Rectangle[][] panRect = new Rectangle[9][9];
    Rectangle[][] grimRect= new Rectangle[9][9];
    
    int[][] su = new int[9][9];
    
    int drow, dcol, brow, bcol;
    
    boolean bFull, bStart;
    
    int count = 0;
    int score = 1000000;
    
    String[][] name;
    int[][] jumsu;
    
    RegDlg rd;
    int a;
    
    public GamePan(String[][] name, int[][] jumsu, JLabel sLabel, JLabel cLabel) {
        chasu=3;
        width=500;
        high=500;
        bFull=true;
        bStart=false;
        
        this.name=name;
        this.jumsu=jumsu;
        
        this.scoreLabel = sLabel;
        this.countLabel = cLabel;
        
        mt.addImage(img,0);
        
        try
        {
        mt.waitForAll();	//MediaTracker에 있는 모든 그림을 로딩한다.
        }
        catch(Exception e){}
        
        getRand();
        getRect();
        
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                width=getWidth();
                high=getHeight();
                
                getRect();
                repaint();
            }
        });
        
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(!(e.getModifiers()==InputEvent.BUTTON1_MASK))return;
                
                // count : 총점에서 감점해 나갈 값.
                // 클릭해서 조각이 이동될 때 마다 count 값을 10씩 증가. 
                count+=1; 
                score -= count*10;
                
                scoreLabel.setText("점수 : " + score);
                countLabel.setText("이동회수 : " + count);
                
                drawCheck(e);
            }
        });
    }
    
    public void drawCheck(MouseEvent e) {
        for(int a=0;a<chasu;a++) {
            for(int b=0;b<chasu;b++) {
                if(panRect[a][b].contains(e.getPoint())) {
                    if(a==brow && b==bcol) return;
                    
                    su[brow][bcol]=su[a][b];
                    su[a][b]=chasu*chasu-1;
                    
                    drow=brow;
                    dcol=bcol;
                    brow=a;
                    bcol=b;
                    
                    repaint();
                    
                    boolean bEnd=endCheck();
                    
                    if(bEnd) {
                        int sel=JOptionPane.showConfirmDialog(this,
                        "축하합니다!! 퍼즐이 완성되었습니다.",
                        "알림!!",
                        JOptionPane.DEFAULT_OPTION);
                        
                        //임시 제거 rankCheck();
                        
                        getRect();
                        getRand();
                        repaint();
                        return;
                    }
                }
            }
        }
    }
    
    void getRand() {
        int[] imsi=new int[81];
        
        int rs;
        boolean b_repeat;
        int rand=-1;
        Random random=new Random();

        for(int a=0;a<chasu*chasu;a++) {
            b_repeat = true;
            while(b_repeat) {
                b_repeat = false;
                rand = random.nextInt(chasu*chasu);

                for(int b=0;b<a;b++) {
                    if(rand == imsi[b]) b_repeat = true;
                }
            }

            imsi[a] = rand;
            su[a/chasu][a%chasu] = rand;
            
            if(rand == chasu*chasu-1) {
                brow = a/chasu;
                bcol = a%chasu;
            }
        }
    }
    
    public void getRect() {
        for(int a=0;a<chasu;a++) {
            for(int b=0;b<chasu;b++) {
                panRect[a][b]=new Rectangle(b*width/chasu, a*high/chasu,
                width/chasu, high/chasu);
                
                grimRect[a][b]=new Rectangle(b*img.getWidth(this)/chasu, 
                a*img.getHeight(this)/chasu,
                img.getWidth(this)/chasu, 
                img.getHeight(this)/chasu);
            }
        }
    }

    // 조각을 다 맞추었는지 체크
    public boolean endCheck() {
        int a,b,n=0;
        for(a=0;a<chasu;a++) {
            for(b=0;b<chasu;b++) {
                if(su[a][b]!=n) return false;
                n++;
            }
        }
        return true;
    }
    
    public void rankCheck() {
    
        for(a=0;a<10;a++) {
            if(jumsu[chasu-3][a]<score)break;
        }
        
        if(a==10) return;
        
        rd=new RegDlg();
        rd.tf_rank.setText(new Integer(a+1).toString());
        rd.tf_count.setText(new Integer(count).toString());
        rd.tf_score.setText(new Integer(score).toString());
        
        for(int b=9;b>a;b--) {
            name[chasu-3][b]=name[chasu-3][b-1];
            jumsu[chasu-3][b]=jumsu[chasu-3][b-1];
        }
        
        rd.setVisible(true);
        
        rd.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                name[chasu-3][a]=rd.tf_name.getText();
                jumsu[chasu-3][a]=score;
            }
        });	
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        
        if(bFull) {
            g.drawImage(img,0,0,width,high,this);
            return;
        }
        
        int x;
        int y;
        
        for(int a=0;a<chasu;a++) {
            for(int b=0;b<chasu;b++) {
                x=su[a][b]/chasu;
                y=su[a][b]%chasu;
                
                g.drawImage(img, 
                panRect[a][b].x, 
                panRect[a][b].y, 
                b*panRect[a][b].width +panRect[a][b].width, 
                a*panRect[a][b].height+panRect[a][b].height, 
                grimRect[x][y].x, 
                grimRect[x][y].y, 
                y*grimRect[x][y].width +grimRect[x][y].width, 
                x*grimRect[x][y].height+grimRect[x][y].height, 
                this);	
            }
        }
        
        g.fillRect(	panRect[brow][bcol].x, 
        panRect[brow][bcol].y, 
        panRect[brow][bcol].width, 
        panRect[brow][bcol].height);
        
    }
    
}