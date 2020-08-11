package jdk14.helpful_nullpointerexceptions;

public class NullPointerExceptionsExample {
    public static void main(String[] args) {
        // -XX:+ShowCodeDetailsInExceptionMessages
        Song song = new Song();
//        song.genre = new Genre();
        System.out.println(song.genre.name.toUpperCase());
    }

    static class Song {
        Genre genre;
    }

    static class Genre {
        String name;
    }
}
