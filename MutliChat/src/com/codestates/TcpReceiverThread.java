package com.codestates;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * TcpReceiverThread는 소켓으로부터 들어오는 메시지를 처리합니다.
 */
public class TcpReceiverThread implements Runnable {
    private Socket socket;

    // 생성자: TcpReceiverThread는 특정 소켓과 연결됩니다.
    public TcpReceiverThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String msg;
            // 소켓으로부터 메시지를 계속해서 읽습니다.
            while ((msg = br.readLine()) != null) {
                System.out.println("받은 메시지: " + msg);
            }
        } catch (IOException e) {
            System.err.println("TcpReceiverThread 오류: " + e.getMessage());
        } finally {
            // 스레드 종료 시 소켓을 닫습니다.
            closeSocket();
        }
    }

    // 소켓을 안전하게 닫는 메서드입니다.
    private void closeSocket() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("소켓 닫기 오류: " + e.getMessage());
        }
    }
}
