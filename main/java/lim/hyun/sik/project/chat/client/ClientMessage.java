package lim.hyun.sik.project.chat.client;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class ClientMessage {
	final static String SERVER_ADDR = "127.0.0.1";	// 주소값 설정!!
	final static int SERVER_PORT = 8010;	//	서버 포트 설정
	static Socket socket;	// socket 정의
	static volatile BufferedReader br;	// 변수가 버퍼를 읽을 값 설정
	static PrintWriter pw;	// 출력 변수 값 설정
	static JButton btnSend;	// 보내기 버튼 설정
	
	
	
	// 상수 필드, 비상수 필드들, 애플리케이션의 GUI 생성을 위한 패널안의 createGUI() 메소드 정의
	/**
	 * @wbp.parser.entryPoint
	 */
	static JPanel createGUI()	
	{
		
		// 메인 패널
		JPanel pnlLayout = new JPanel();
		
		
		
		pnlLayout.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		pnlLayout.setLayout(null);	// 패널 레이아웃

		/* 윗쪽 패널 설정 */
		JPanel pnlup = new JPanel();
		pnlup.setBounds(0, 12, 389, 64);
		pnlLayout.add(pnlup);
		pnlup.setLayout(null);
		
		// 유저 이름 설정!!
		final JTextField txtUsername = new JTextField();
		pnlup.add(txtUsername);
		txtUsername.setEditable(true);
		txtUsername.setBounds(14, 12, 131, 45);
		txtUsername.requestFocus();
	
		/* 끝  */
		
		
		/* 오른쪽 패널 설정 */
		JPanel pnlli = new JPanel();
		pnlli.setBounds(270, 125, 111, 232);
		pnlLayout.add(pnlli);
		pnlli.setLayout(null);
		
		// 방문자 목록 출력
		final JTextArea list = new JTextArea();
		list.setLineWrap(true);
		list.setEditable(false);
		list.setBounds(0, 0, 111, 232);
		pnlli.add(list);
		//list.setColumns(10);
		
		/* 끝  */
		
		// 채팅내용 라벨
		final JLabel lblNewLabel;				// 채팅 내용 표시하는 라벨
		lblNewLabel = new JLabel("\uB300\uD654 \uB0B4\uC6A9");
		lblNewLabel.setBounds(20, 84, 217, 29);
		pnlLayout.add(lblNewLabel);		
		
		/* 왼쪽 패널 설정 */
		JPanel pnlmg = new JPanel();
		pnlmg.setBounds(20, 125, 217, 232);
		pnlLayout.add(pnlmg);
		pnlmg.setLayout(null);
		
		
		// 메시지 내용 출력
		final JTextArea ta_msgRoom = new JTextArea();
		ta_msgRoom.setLineWrap(true);
		ta_msgRoom.setEditable(false);
		ta_msgRoom.setBounds(0, 0, 217, 231);
		pnlmg.add(ta_msgRoom);
		ta_msgRoom.setColumns(50);
		
		/* 끝  */
		

		// 방문자 목록 라벨
		final JLabel label = new JLabel("방문자 목록");
		label.setBounds(270, 87, 106, 23);
		pnlLayout.add(label);

		
		
		/* 아랫쪽 패널 설정 */
		JPanel pnltf = new JPanel();
		pnltf.setBounds(14, 373, 362, 38);
		pnlLayout.add(pnltf);
		
		
		// 보낼 메시지 입력
		final JTextField tf_sendMsg = new JTextField();
		tf_sendMsg.setBounds(0, 0, 247, 38);
		tf_sendMsg.setText("\uBCF4\uB0BC \uBA54\uC138\uC9C0\uB97C \uC785\uB825\uD574\uC8FC\uC138\uC694.");
		tf_sendMsg.setEnabled(false);
		tf_sendMsg.setToolTipText("");
		tf_sendMsg.setColumns(10);
		tf_sendMsg.requestFocus();
		
		//보내기 엔터키 활성화!!
		tf_sendMsg.addActionListener(new ActionListener()
		{
			/* (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				pw.println(tf_sendMsg.getText());	//.ta_msgRoom 창에 뜨게 하기 <메신저 내용에 떠야하는데 여기서 방문자 목록에서 뜸>
										// 만일 ta_msgRoom.append(tf_sendMsg.getText()); 사용시 소켓 스레드에서 절때
										// 전송이 안되고 단순 스윙 기능으로 실행이 되어지므로 채팅프로그램 쓸 때 타인은 절 때 메신저 수신 불가
				tf_sendMsg.setText("");
				
			}
		});

		
		//tf_sendMsg.action(arg0, arg1);
		
		pnltf.setLayout(null);
		pnltf.add(tf_sendMsg);
		
		/* 끝  */
		
		//접속하기 버튼
		final JButton btnConnect = new JButton("연결하기");
		btnConnect.setBounds(161, 12, 95, 45);
		
		ActionListener al;	// 버튼 활성화를 위해 al로 변수로 지정
		al = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)	
			{
				txtUsername.setFocusable(false);	// 유저 이름 비활성화
				String username = txtUsername.getText().trim();	// 유저이름 입력한거 
				try
				{
					socket = new Socket(SERVER_ADDR, SERVER_PORT);
					btnConnect.setEnabled(false);	// 연결하기 버튼 비활성화
					txtUsername.setEditable(false);
					
					// 패키지 안의 버퍼 내용 출력을 위해 버퍼 저장 과정!!
					InputStreamReader isr;
					isr = new InputStreamReader(socket.getInputStream());	// isr에 서버 포트에서 입력내용 저장
					br = new BufferedReader(isr);	// 버퍼 읽는 변수 저장
					pw = new PrintWriter(socket.getOutputStream(), true);	// 버퍼 내용 출력
					ta_msgRoom.append(username + br.readLine() + "\n");	// ~~님이 접속을 환영습니다.			
					pw.println((!username.equals(""))?username:"unknown");	// 여기서 버퍼내용 username 이퀄즈 내용 저장 하지 않는다.
					
					//채팅프로그램 실행 기능 실행!! 
					tf_sendMsg.setEnabled(true);
					btnSend.setEnabled(true);
					
					// 서버로 전달되는 함수 코딩!!
					new Thread(new Runnable(){
						@Override
						public void run()
						{
							String line;
							try
							{//list.append(str); <- connection 추가!!
								while ((line = br.readLine()) !=null)	// line 값이 버퍼에 저장된값에 라인이 없지 않을 때까지
								{
									if(line.charAt(0) != '!')	// 문자열 길이가 0이  !이 아닐씨!
									{
										ta_msgRoom.append(line + "\n");
										continue;
									}
									list.setText("");	// txtuser -> list
									String[] users;
									users = line.substring(1).split(" ");	// 공백제거 문자열 두번째부터 출력
									for (String user: users)
									{
										list.append(user);	// txtuser -> list
										list.append("\n");	// txtuser -> list
									}
								}
							}
							catch(IOException ioe)
							{
								ta_msgRoom.append("링크를 연결 할 수가 없다.");
								return;
							}
						}
					}).start();
				}
				catch(Exception e)
				{
					ta_msgRoom.append("서버에 연결 할 수가 없다.");
				}
				
			}
		};
		
		btnConnect.addActionListener(al);
		pnlup.add(btnConnect);
		btnSend = new JButton("보내기");
		btnSend.setFont(new Font("굴림", Font.PLAIN, 17));
		btnSend.setEnabled(false);
			
		al = new ActionListener()
		{
			/* (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent ae)
			{

				
				pw.println(tf_sendMsg.getText());	//.ta_msgRoom 창에 뜨게 하기 <메신저 내용에 떠야하는데 여기서 방문자 목록에서 뜸>
										// 만일 ta_msgRoom.append(tf_sendMsg.getText()); 사용시 소켓 스레드에서 절때
										// 전송이 안되고 단순 스윙 기능으로 실행이 되어지므로 채팅프로그램 쓸 때 타인은 절 때 메신저 수신 불가
				tf_sendMsg.setText("");
				
			}
		};	//
		btnSend.addActionListener(al);
		JPanel pnlstb = new JPanel();
		pnlstb.setBounds(261, 0, 101, 38);
		pnlLayout.add(pnltf);
		pnltf.add(pnlstb);
		pnlstb.setLayout(new GridLayout(0, 1, 0, 0));
		pnlstb.add(btnSend);
		
		JButton btnQuit = new JButton("종료하기");
		al = new ActionListener() {	
			// 종료하기 버튼 이벤트
			@Override
			public void actionPerformed(ActionEvent ae)
			{
	
				try {		
					if (socket !=null)
						socket.close();
				}
				catch(IOException ioe)	{ }
				System.exit(0);
			}
		};
		

		btnQuit.addActionListener(al);
		btnQuit.setBounds(270, 12, 105, 44);
		pnlup.add(btnQuit);
		
		return pnlLayout;
    }



    public static void main(String[] args) {
        Runnable r = new Runnable() {

            @Override
            public void run()
            {
                JFrame f = new JFrame("키위스무디의 메신저");
                f.addWindowListener(new WindowAdapter (){
                    public void windowClosing(WindowEvent e) {
                        try{
                            if (socket !=null)
                                socket.close();
                        }catch(IOException ioe){
                        }
                        System.exit(0);
                    }
                });

                
                //f.setUndecorated(true);	// 타이틀바 제거
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f.setContentPane(createGUI());
                f.pack();
                f.setResizable(false);
                f.setVisible(true);
                f.setBounds(400, 300, 400, 480);
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Image img = toolkit.getImage("images.jpg");
                f.setIconImage(img);// <이미지 넣을것>

            }
        };
        EventQueue.invokeLater(r);
	}
}
