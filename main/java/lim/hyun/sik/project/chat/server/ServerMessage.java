package lim.hyun.sik.project.chat.server;

import java.net.*;
import java.io.*;
import java.util.*;

public class ServerMessage {
	private final static int PORT_NO = 8010;
	private ServerSocket listener;
	private List<Connection> clients;	// list에 Connection 파라미터 변수 설정!! - 객체 만들기용 -> 일명 자료구조 클래스!!
										// 여기서 무조건 클라이언트 아이디를 저장하는 과정을 낸다.
	
	ServerMessage() throws IOException	//
	{
		listener = new ServerSocket(PORT_NO);
		clients = new ArrayList<>();	// 클래스 객체 내부에 있는 배열에 데이터를 저장!! // 여기서 LinkedList도 있다. 인접 데이터들이 서로 가리키는 식으로 데이터 저장
		System.out.println("listening on port " + PORT_NO);
	}
	// 1. 메인 실행
	void runServer()	// 무한 반복을 위한 메소드를 사용한다.
	{
		try
		{
		
			while(true){	// 다중 채팅을 할 수 있게 무한 루프를 돌렸다.(여기서 무한 루프를 빼면 1:1 채팅 프로그램이 된다.)
				Socket socket = listener.accept();	// 클라이언트가 요청을 받을때까지 기달린다.
				System.out.println("클라이언트로 부터 연결되었습니다. ");	// 콘솔에 알림창 표시
				Connection con = new Connection(socket);	// 커넥션 역할을 con 으로 변수 지정 후 실행!!
				synchronized(clients)
				{
					clients.add(con);		// 클라이언트 연결!!
					con.start();			// 연결 시작!!

						con.send(" 님 접속을 환영합니다.");	// message 함수에 저장하여 그대로 전송	socket.getInetAddress <- 서버 IP 확인
					//	System.out.println(clients.getInactiveAddress());	// 서버 IP 콘솔 출력!!
				}
			}
		}
		catch(IOException ioe)
		{
			System.err.println("I/O error : " + ioe.getMessage());	// 링크를 할 수 없다는 메신저창 표시
			return;
		}
	}
	
	//2. 28번째 줄실행
	private class Connection extends Thread // 연결하는 쓰레드 가동 함수!!
	{
		private volatile BufferedReader br;
		private volatile PrintWriter pw;
		private String clientName;
		Connection(Socket s) throws IOException	// 소켓에 연결, 예외값 출력
		{
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));	// 입력한 값을 저장하는 스트림 생성!!
			pw = new PrintWriter(s.getOutputStream(), true);		// 저장된 버퍼를 그대로 출력!!
		}
		@Override	// 겹쳐서 포함 실행
		public void run()	// 실행 메소드
		{
			String line;
			try				// 들어왔다는 내용을 보내준다.
			{
				clientName = br.readLine();
				sendClientsList();		// 클라이언트로 보내는 메소드
				while ((line=br.readLine()) !=null)	// 	무한 반복!!(멀티쓰레드)
					broadcast(clientName + ":" + line);	// 메시지 입력 내용 표시
			
			}
			catch(IOException ioe)	// 시도시 실패이유를 표시!!
			{
				System.err.println("I/O error : " + ioe.getMessage());	// 에러메시지 표시
			}
			finally		// 마지막으로 클라이언트 종료 표시!!
			{
				System.out.println(clientName + " : " + "finished");	// 클라이언트 이름을 마친다
				synchronized(clients){
					
					clients.remove(this);	// 나간 유저항목에서 지운다.
					broadcast(clientName + "님이 나갔습니다." + "\n" +"now " + clients.size() + " users");
					
					sendClientsList();
					
				}
			}
		}
		
		//4. 알림의 메서드
		private void broadcast(String message)	// 알림 설정!!
		{
			System.out.println("알림 " + message);
			synchronized(clients)
			{
				for (Connection con : clients)	// 클라이언트에 계속 보낸다.
					con.send(message);	// 98번째 줄 pw.println(message); 연결된 클라리언트의 대한 메세지 출력한다.
			}
		}
		
		//5. 93번째줄 보내는 메서드 -- 메시지 보내기!!
		private void send(String message)
		{
			pw.println(message);
		}
		
		//3. 66번째 함수 실행!! 클라이언트에게 보내는 메서드
		private void sendClientsList()	// 클라이언트 리스트를 보낸다.
		{
			StringBuilder sb = new StringBuilder();	// 문자열 빌더
			synchronized(clients)
			{
				for (Connection con : clients)
				{
					sb.append(con.clientName);
					sb.append(" ");
				}
				broadcast("!" + sb.toString()); 
			}
		}	//
	}
	
	
	public static void main(String[] args)
	{
		try
		{
			System.out.println("서버 스타트!");
			new ServerMessage().runServer();	// 서버에서 클라이언트와의 채팅을 위해 메신저로 들어가고 접속한 인원을 뽑아낸다.
		}
		catch(IOException ioe)
		{
			System.err.println("unable to create server socket");	// 서버를 두번 이상 실행시!!
		}
	}

}
