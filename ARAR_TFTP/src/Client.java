import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import Enum.*;

/**
 * Created by Brandon on 06/05/2017.
 */
public class Client {

    private DatagramSocket clientSocket;
    private byte[] buffer;
    private byte[] fileDatas;

    private int serverPort = 69;
    private InetAddress serverIp;


    public byte[] paquetWRRQ(String fileName, int operationCode){

        byte[] buffer = new byte[ 512 + 4 ];
        byte[] opCode = new byte[]{0,(byte)operationCode};
        byte[] filename = fileName.getBytes();
        byte[] fillingBytes = new byte[]{0};
        byte[] type = "octet".getBytes();

        int i = 0;

        System.arraycopy(opCode,0,buffer,i,2);
        i+=2;
        System.arraycopy(filename,0,buffer,i,filename.length);
        i+=filename.length;
        System.arraycopy(fillingBytes,0,buffer,i,1);
        i++;
        System.arraycopy(type,0,buffer,i,type.length);
        i+=type.length;
        System.arraycopy(fillingBytes,0,buffer,i,1);

        return buffer;

    }


    public int sendFile(String path, String fileName, String fileNameOnServer, String ipAdress, int port){

        DatagramPacket WRQPacket;
        DatagramPacket receivedPacket;

        FileInputStream file = null;
        int i = 0;
        int j = 0;
        int ttl = 0;


        try{
            // Ouverture de la communication avec le serveur
            // (Ouverture du socket)
            serverIp = InetAddress.getByName(ipAdress);
            clientSocket = new DatagramSocket();

            // On envoie un WRQ
            buffer = paquetWRRQ(fileNameOnServer,PacketType.WRQ.code);
            WRQPacket = new DatagramPacket(buffer, buffer.length,serverIp,port);
            clientSocket.send(WRQPacket);

            // Reception du ACK
            byte[] receptionBuffer = new byte[4];
            receivedPacket = new DatagramPacket(receptionBuffer,receptionBuffer.length);
            clientSocket.receive(receivedPacket);


            // Si on reçoit bien un ACK en retour
            if (receptionBuffer[1] == PacketType.ACK.code){

                // On récupère le port alloué part le serveur pour cette communication
                serverPort = receivedPacket.getPort();

                // On clear les buffers
                buffer = new byte[516];
                fileDatas = new byte[512];


                // On essaye d'ouvrir le fichier
                try{
                    file = new FileInputStream(path+"/"+fileName);
                }catch (Exception ex){
                    System.out.println("Erreur, impossible d'ouvrir le fichier");
                    return -1 ;
                }

                // Tant qu'on a pas lu tout le fichier
                while (file.read(fileDatas)>0){
                    buffer[0] = (byte) 0;
                    buffer[1] = (byte) PacketType.DATA.code ;
                    buffer[2] = (byte) j ;
                    buffer[3] = (byte) (i+1);

                    // On place les 512 octets de données dans le buffer après l'entête de 4octets.
                    for(int x=0 ; x < fileDatas.length ; x++ ){
                        buffer[4+x] = fileDatas[x];
                    }


                    // Tant qu'on ne reçoit pas le bon ACK, on renvoie le paquet
                    int verifACK = -1 ;
                    DatagramPacket fileData = new DatagramPacket(buffer,buffer.length,serverIp,serverPort);

                    while (verifACK != (i+1) || receptionBuffer[1] != PacketType.ACK.code){

                        // On envoi du paquet
                        clientSocket.send(fileData);
                        receptionBuffer = new byte[516];

                        // Réception ACK
                        receivedPacket = new DatagramPacket(receptionBuffer, receptionBuffer.length);
                        clientSocket.receive(receivedPacket);
                        verifACK = receptionBuffer[3];

                        if (verifACK < 0){
                            verifACK = 256 - Math.abs(receptionBuffer[3]);
                        }
                        ttl++;
                        if (ttl > 30 ){
                            System.out.println("ERROR -2 : Dépassement de délai");
                            return (-2) ;
                        }
                    }

                    ttl = 0 ;
                    i++;
                    if (i == 255){
                        j++;
                        i = -1;
                    }
                    buffer = new byte[516];
                    fileDatas = new byte[512];
                }

                //Envoi d'un dernier paquet vide pour les fichier multiple de 512
                buffer = new byte[4];
                buffer[0] = (byte) 0;
                buffer[1] = (byte) PacketType.DATA.code;
                buffer[2] = (byte) j;
                buffer[3] = (byte) (i+1);
                DatagramPacket donneesDernierPacket = new DatagramPacket(buffer, buffer.length, serverIp, serverPort);
                clientSocket.send(donneesDernierPacket);
            }

            else if (receptionBuffer[1] == PacketType.ERROR.code){
                System.out.println(ServerError.getStringFromValue(receptionBuffer[3]).libelle);
                //vue.getTextInfoArea().append(ServerError.getStringFromValue(receptionBuffer[3]).libelle + "\n");
                return receptionBuffer[3];
            }

            System.out.println("Fichier envoyé");
            //vue.getTxtInfoArea().append("Fichier envoyé\n");
            file.close();

        } catch (UnknownHostException e) {
            System.out.println("Erreur -1 : IP indéterminée");
            //vue.getTxtInfoArea().append("Erreur -1 : IP indéterminée\n");
            return -1;
        } catch (SocketException e) {
            System.out.println("Erreur -2 : Problème de création ou d'accès au socket");
            //vue.getTxtInfoArea().append("Erreur -2 : Problème de création ou d'accès au socket\n");
            return -2;
        } catch (IOException e) {
            System.out.println("Erreur -3 : Problème réseau");
            //vue.getTxtInfoArea().append("Erreur -3 : Problème réseau\n");
            return -3;
        } catch (StackOverflowError e) {
            System.out.println("Erreur -5 : Espace disque insuffisant");
            //vue.getTxtInfoArea().append("Erreur -5 : Espace disque insuffisant\n");
            return -5;
        } catch (Exception e) {
            System.out.println("Erreur -6 : Problème inconnu");
            //vue.getTxtInfoArea().append("Erreur -6 : Problème inconnu\n");
            return -6;
        }finally {
            clientSocket.close();
        }
        return 0;
    }


}
