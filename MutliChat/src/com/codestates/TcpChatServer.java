package com.codestates;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// TcpChatServerManager는 클라이언트 연결 관리와 메시지 브로드캐스팅을 담당합니다.
class TcpChatServerManager {
    private List<Socket> socketList;

    public TcpChatServerManager() {
        socketList = new ArrayList<>();
    }

    // 새로운 클라이언트 소켓을 리스트에 추가하고, 수신 스레드를 시작합니다.
    public void addSocket(Socket socket) {
        this.socketList.add(socket);
        new Thread(new ReceiveThread(socket)).start();
    }

    // ReceiveThread는 들어오는 메시지를 처리하고 모든 클라이언트에게 브로드캐스트합니다.
    class ReceiveThread implements Runnable {
        private Socket socket;

        public ReceiveThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String msg;
                while ((msg = br.readLine()) != null) {
                    broadcastMessage(msg);
                }
            } catch (IOException e) {
                System.err.println("ReceiveThread 오류: " + e.getMessage());
            } finally {
                removeSocket(socket);
            }
        }

        // 메시지를 모든 클라이언트에게 브로드캐스트하는 메서드입니다.
        private void broadcastMessage(String message) {
            for (Socket s : socketList) {
                try {
                    if (!s.isClosed()) {
                        s.getOutputStream().write((message + "\\n").getBytes());
                    }
                } catch (IOException e) {
                    System.err.println("메시지 브로드캐스팅 오류: " + e.getMessage());
                }
            }
        }

        // 소켓을 리스트에서 제거하고 닫는 메서드입니다.
        private void removeSocket(Socket socket) {
            socketList.remove(socket);
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("소켓 닫기 오류: " + e.getMessage());
            }
        }
    }
}

public class TcpChatServer {
    public static void main(String[] args) {
        int port = 4444; // 기본 포트 번호
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("유효하지 않은 포트 번호. 기본 포트를 사용합니다.");
            }
        }

        // 서버 소켓을 생성하고 클라이언트 연결을 기다립니다.
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            TcpChatServerManager manager = new TcpChatServerManager();
            System.out.println(port + "포트에서 서버 시작");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                manager.addSocket(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("서버 시작 오류: " + e.getMessage());
        }
    }
}
