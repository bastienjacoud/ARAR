import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import Enum.*;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import sun.nio.cs.ext.PCK;

/**
 * Created by Brandon on 06/05/2017.
 */
public class Client {

    private DatagramSocket clientSocket;
    private byte[] buffer;
    private byte[] fileDatas;

    private int serverPort = 69;
    private InetAddress serverIp;

    private Vue vue;

    public Client(Vue ait){
        this.vue = ait;
    }

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

                vue.getTxtInfoArea().append("Serveur - "+serverIp+":"+serverPort+"\n");
                vue.repaint();

                // On clear les buffers
                buffer = new byte[516];
                fileDatas = new byte[512];


                // On essaye d'ouvrir le fichier
                try{
                    file = new FileInputStream(path+"/"+fileName);
                    vue.getTxtInfoArea().append("Ouverture du fichier réussi\n");
                    vue.repaint();
                }catch (Exception ex){
                    vue.getTxtInfoArea().append("Erreur -1 : Echec de l'ouverture du fichier\n");
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
                            vue.getTxtInfoArea().append("Erreur -2 : Dépassement de délai\n");
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
                vue.getTxtInfoArea().append( ServerError.getStringFromValue(receptionBuffer[3]).libelle +"\n");
                System.out.println(ServerError.getStringFromValue(receptionBuffer[3]).libelle);
                //vue.getTextInfoArea().append(ServerError.getStringFromValue(receptionBuffer[3]).libelle + "\n");
                return receptionBuffer[3];
            }

            System.out.println("Fichier envoyé");
            //vue.getTxtInfoArea().append("Fichier envoyé\n");
            file.close();

        } catch (UnknownHostException e) {
            System.out.println("Erreur -1 : IP indéterminée");
            vue.getTxtInfoArea().append("Erreur -1 : IP indéterminée\n");
            return -1;
        } catch (SocketException e) {
            System.out.println("Erreur -2 : Problème de création ou d'accès au socket");
            vue.getTxtInfoArea().append("Erreur -2 : Problème de création ou d'accès au socket\n");
            return -2;
        } catch (IOException e) {
            System.out.println("Erreur -3 : Problème réseau");
            vue.getTxtInfoArea().append("Erreur -3 : Problème réseau\n");
            return -3;
        } catch (StackOverflowError e) {
            System.out.println("Erreur -5 : Espace disque insuffisant");
            vue.getTxtInfoArea().append("Erreur -5 : Espace disque insuffisant\n");
            return -5;
        } catch (Exception e) {
            System.out.println("Erreur -6 : Problème inconnu");
            vue.getTxtInfoArea().append("Erreur -6 : Problème inconnu\n");
            return -6;
        }finally {
            clientSocket.close();
        }
        return 0;
    }


    public int receiveFile(String fileName, String path, String localFileName, String address, int port){

        DatagramPacket RRQPacket;
        DatagramPacket data;
        DatagramPacket ACK;
        FileOutputStream file;
        boolean receptionOver = false;

        try{
            // Ouverture de la connexion sur le premier port libre
            int freePort = Commun.firstAvailablePort(1024,2048);
            this.clientSocket = new DatagramSocket(freePort);
            this.serverIp = InetAddress.getByName(address);
            this.serverPort = port;

            vue.getTxtInfoArea().append("Connecté au serveur  " + serverIp + ":" + serverPort+"\n");
            vue.repaint();

            // Envoi du RRQ
            buffer = paquetWRRQ(fileName,PacketType.RRQ.code);
            RRQPacket = new DatagramPacket(buffer,buffer.length,serverIp,serverPort);
            clientSocket.send(RRQPacket);

            // Création du fichier
            File localFile = new File(path+localFileName);

            // S'il existe déjà dans le répertoire
            if (localFile.exists()){
                vue.getTxtInfoArea().append("Erreur -4 : Le fichier "+(path+localFileName)+" existe déjà dans ce répertoire.\n");
                return -4;
            }

            // Ouverture d'un flux de données entrantes
            file = new FileOutputStream(path+localFileName);
            vue.getTxtInfoArea().append("Création du fichier réussie \n");
            vue.repaint();

            while (!receptionOver){

                // Réception du packet de donnée
                buffer = new byte[516];
                data = new DatagramPacket(buffer,buffer.length);
                clientSocket.receive(data);
                serverPort = data.getPort();

                // Si le packet contient des données :
                if (buffer[1] == PacketType.DATA.code){


                    // On récupère les données reçue et on les écrit dans le fichier
                    for(int x = 4 ; x < buffer.length ; x++){
                        file.write(buffer[x]);
                    }
                    byte blockNumber = buffer[2];
                    byte blockNumber2 = buffer[3];

                    // On construit un ACK
                    buffer = new byte[516];
                    buffer[0] = 0;
                    buffer[1] = (byte) PacketType.ACK.code;
                    buffer[2] = blockNumber;
                    buffer[3] = blockNumber2;

                    ACK = new DatagramPacket(buffer,buffer.length,serverIp,serverPort);
                    clientSocket.send(ACK);

                    receptionOver = data.getLength()<512 ;

                }

                // Sinon on renvoi l'erreur que le serveur nous a envoyé
                else if (buffer[1] == PacketType.ERROR.code){

                    System.out.println(ServerError.getStringFromValue(buffer[3]).libelle);
                    vue.getTxtInfoArea().append(ServerError.getStringFromValue(buffer[3]).libelle +"\n");
                    return buffer[3];

                }

            }
            vue.getTxtInfoArea().append("Le fichier à bien été reçu \n");
            file.close();


        } catch (UnknownHostException e){

            vue.getTxtInfoArea().append("Erreur -1 : IP indéterminée\n");
            System.out.print("Erreur -1 : Ip indéterminée ");
            e.printStackTrace();
            return -1;

        } catch (SocketException e){
            vue.getTxtInfoArea().append("Erreur -2 : Problème de création ou d'accès au socket\n");
            System.out.println("Erreur -2 : Problème de création ou d'accès au socket");
            e.printStackTrace();
            return -2;
        } catch (IOException e){
            vue.getTxtInfoArea().append("Erreur -3 : Problème réseau\n");
            System.out.println("Erreur -3 : Problème réseau");
            e.printStackTrace();
            return -3;
        } catch (StackOverflowError e) {
            vue.getTxtInfoArea().append("Erreur -5 : Espace disque insuffisant\n");
            System.out.println("Erreur -5 : Espace disque insuffisant");
            e.printStackTrace();
            return -5;
        } catch (Exception e) {
            vue.getTxtInfoArea().append("Erreur -6 : Problème inconnu\n");
            System.out.println("Erreur -6 : Problème inconnu");
            e.printStackTrace();
            return -6;
        } finally {
            clientSocket.close();
        }
        return 0;
    }

}
