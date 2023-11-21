package com.codestates;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * TcpSenderThread는 사용자의 메시지를 소켓을 통해 전송합니다.
 */
public class TcpSenderThread implements Runnable {
    private Socket socket;

    // 생성자: TcpSenderThread는 특정 소켓과 연결됩니다.
    public TcpSenderThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             Scanner scan = new Scanner(System.in)) {
            // 사용자 입력을 계속해서 읽고 소켓을 통해 전송합니다.
            while (true) {
                String msg = scan.nextLine();
                if ("quit".equals(msg)) {
                    break;
                }
                bw.write(msg + "\\n");
                bw.flush();
            }
        } catch (IOException e) {
            System.err.println("TcpSenderThread 오류: " + e.getMessage());
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
