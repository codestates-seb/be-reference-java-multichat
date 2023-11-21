package com.codestates;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class TcpChatClient {
    public static void main(String[] args) {

        // 명령줄 인수를 통해 IP 주소와 포트 번호를 확인합니다.
        if (args.length < 2) {
            System.out.println("사용 방법: java TcpChatClient <IP 주소> <포트 번호>");
            return;
        }

        String ipAddress = args[0];
        int port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(InetAddress.getByName(ipAddress), port)) {
            // 메시지 송수신을 위한 스레드를 생성하고 시작합니다.
            TcpSenderThread senderThread = new TcpSenderThread(socket);
            TcpReceiverThread receiverThread = new TcpReceiverThread(socket);

            new Thread(senderThread).start();
            new Thread(receiverThread).start();
        } catch (NumberFormatException e) {
            System.err.println("오류: 포트 번호는 정수여야 합니다.");
        } catch (IOException e) {
            System.err.println("서버 연결 오류: " + e.getMessage());
        }
    }
}
