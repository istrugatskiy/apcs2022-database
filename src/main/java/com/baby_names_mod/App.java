package com.baby_names_mod;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import com.google.common.collect.Lists;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

public final class App {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        final InputStream serviceAccount = new FileInputStream("api-key.json");
        final GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        final FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();
        FirebaseApp.initializeApp(options);

        final Firestore db = FirestoreClient.getFirestore();
        loadDatabase(db);
    }

    /**
     * Populates the database with the names from the given file.
     * 
     * @param db The database to populate.
     * @throws IOException          An exception if the names.csv file isn't found.
     * @throws ExecutionException   Shouldn't happen.
     * @throws InterruptedException Shouldn't happen.
     */
    private static void loadDatabase(final Firestore db) throws IOException, InterruptedException, ExecutionException {
        final LinkedList<PersonData> data = loadData();
        // We can't have more than 500 documents in a batch.
        // This partitions it.
        final List<List<PersonData>> chunks = Lists.partition(data, 500);
        for (final List<PersonData> _data : chunks) {
            final WriteBatch batch = db.batch();
            final CollectionReference ref = db.collection("names");
            for (final PersonData person : _data) {
                final DocumentReference doc = ref.document(person.name);
                batch.set(doc, person);
            }
            // Commits the batch.
            // What we did before was create a batch,
            // now we actually commit it to the server.
            batch.commit().get();
        }
    }

    /**
     * Loads the data from a csv.
     * 
     * @return The parsed person's data.
     * @throws IOException An IOException if the file isn't found.
     */
    private static LinkedList<PersonData> loadData() throws IOException {
        // Get all lines from the file named names.csv.
        // 4429 names in database.
        final LinkedList<PersonData> names = new LinkedList<>();
        final List<String> lines = Files.readAllLines(Paths.get("names.csv"));
        // Skip the first line.
        for (int i = 1; i < lines.size(); i++) {
            final String line = lines.get(i);
            final Scanner scanner = new Scanner(line);
            scanner.useDelimiter(",");
            final String name = scanner.next();
            final List<Integer> years = new LinkedList<>();
            while (scanner.hasNext()) {
                years.add(scanner.nextInt());
            }
            names.add(new PersonData(name, years));
            scanner.close();
        }
        return names;
    }
}
