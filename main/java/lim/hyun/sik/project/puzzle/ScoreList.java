import lim.hyun.sik.project.puzzle;

class ScoreList extends JFrame implements ItemListener {
    JLabel l1=new JLabel("난이도별");
    JComboBox com=new JComboBox();

    String[][] data=new String[10][3];
    String[] cols={"등수","이름","점수"};

    DefaultTableModel mod=new DefaultTableModel(data,cols);
    JTable table=new JTable(mod);

    String[][] name;
    int[][] jumsu;
    int chasu;

    public ScoreList(String[][] name, int[][] jumsu, int chasu) {
        super("리스트");

        this.name=name;
        this.jumsu=jumsu;
        this.chasu=chasu;

        Container cp=getContentPane();
        JScrollPane sp=new JScrollPane(table);

        com.addItem("3 X 3");
        com.addItem("4 X 4");
        com.addItem("5 X 5");


        JPanel p=new JPanel();
        p.add(l1);
        p.add(com);

        cp.add("North",p);
        cp.add("Center",sp);

        for(int a=0;a<10;a++) {
            data[a][0]=new Integer(a+1).toString();
            data[a][1]=name[chasu][a];
            data[a][2]=new Integer(jumsu[chasu][a]).toString();
        }

        table.setModel(new DefaultTableModel(data,cols));

        pack();

        com.addItemListener(this);
    }

    public void itemStateChanged(ItemEvent e) {
        chasu=com.getSelectedIndex();

        for(int i=0;i<10;i++) {
            for(int j=0;j<3;j++) {
                data[i][j]="";
            }
        }

        for(int a=0;a<10;a++) {
            data[a][0]=new Integer(a+1).toString();
            data[a][1]=name[chasu][a];
            data[a][2]=new Integer(jumsu[chasu][a]).toString();
        }

        table.setModel(new DefaultTableModel(data,cols));

    }
}


