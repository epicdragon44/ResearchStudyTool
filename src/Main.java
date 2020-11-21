import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        ArrayList<Artist> artists = new ArrayList<>(); //list of artists, each whose songs are only theirs
        ArrayList<Song> songs = new ArrayList<>(); //master list of all songs

        Scanner sc = new Scanner(new File("input.txt"));
        sc.nextLine(); sc.nextLine();
        while (sc.hasNextLine()) {
            String[] input = sc.nextLine().split(", ");
            Song song = new Song(input[0], input[1], Double.parseDouble(input[2]), Double.parseDouble(input[3]), Boolean.parseBoolean(input[4]));
            songs.add(song);
            Artist artist = new Artist(input[1]);
            if (!artists.contains(artist)) {
                artists.add(artist);
                artists.get(0).songs.add(song);
            } else {
                artists.get(artists.indexOf(artist)).songs.add(song);
            }
        }
        //end of data input

        for (Artist a : artists) {
            a.init();
        }
        for (Song s : songs) {
            s.init(artists);
        }
        //end of data calculations

        ArrayList<Song> dropSongs = new ArrayList<>();
        ArrayList<Song> nodropSongs = new ArrayList<>();
        System.out.println("PICS scores: ");
        for (Song s : songs) {

            System.out.println(s.name + " has a PICS score of "+ s.PICS);

            if (s.containsDrop) {
                dropSongs.add(s);
            } else {
                nodropSongs.add(s);
            }
        }
        System.out.println("");
        //now we've split our songs up into two sets, those with drops and those without

        double n1 = dropSongs.size();
        //calculate avg PICS for songs with drops
        double droptotal1 = 0;
        for (Song s : dropSongs) {
            droptotal1+=s.PICS;
        }
        double mean1 = droptotal1/n1;
        //calculate var2 for songs with drops
        double droptotal2 = 0;
        for (Song s : dropSongs) {
            droptotal2+=(s.PICS - mean1)*(s.PICS - mean1);
        }
        double var1 = droptotal2 / (n1-1);

        double n2 = nodropSongs.size();
        //calculate avg PICS for songs with drops
        double droptotal3 = 0;
        for (Song s : nodropSongs) {
            droptotal3+=s.PICS;
        }
        double mean2 = droptotal3/n2;
        //calculate var2 for songs without drops
        double droptotal4 = 0;
        for (Song s : nodropSongs) {
            droptotal4+=(s.PICS - mean1)*(s.PICS - mean1);
        }
        double var2 = droptotal4 / (n2-1);

        double T = (mean1-mean2) / ((((n1-1)*(var1*var1)+(n2-1)*(var2*var2))/(n1+n2-2))*Math.sqrt(1/n1 + 1/n2));
        double df = n1 + n2 - 2;

        System.out.println("T-score T: " + T);
        System.out.println("Degrees of Freedom df: " + df);
        System.out.println("Now, reference your stats against a T-table!");
    }
}

 class Artist {
    public String name;
    public ArrayList<Song> songs;

    public double AIS;
    public double S2;

     public Artist(String name) {
         this.name = name;
         this.songs = new ArrayList<>();
     }

     @Override
     public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;
         Artist artist = (Artist) o;
         return Objects.equals(name, artist.name);
     }

     @Override
     public int hashCode() {
         return Objects.hash(name, songs);
     }

     public void init() {
         double total1 = 0;
         for (Song s : songs) {
             total1+=s.IS;
         }
         AIS = total1 / songs.size();

         double total2 = 0;
         for (Song s : songs) {
             total2+=(s.IS - AIS)*(s.IS - AIS);
         }
         S2 = total2 / (songs.size()-1);

         System.out.println(name + "'s music has an AIS of " + AIS + " with S^2 of " + S2);
         System.out.println("");
     }
 }

 class Song {
    public String name;
    public String artist;
    public double spotifyCnt;
    public double youtubeCnt;
    public boolean containsDrop;

    public double IS;

    public double PICS;

     public Song(String name, String artist, double spotifyCnt, double youtubeCnt, boolean containsDrop) {
         this.name = name;
         this.spotifyCnt = spotifyCnt;
         this.youtubeCnt = youtubeCnt;
         this.artist = artist;
         this.containsDrop = containsDrop;
         this.IS = ( youtubeCnt+spotifyCnt ) / 2.0;
     }

     public void init(ArrayList<Artist> artists) {
         Artist artistObj = artists.get(artists.indexOf(new Artist(artist)));
         PICS = (IS - artistObj.AIS) / (artistObj.S2);
     }
 }