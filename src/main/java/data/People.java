package data;

import java.util.*;
import java.util.stream.Collectors;

public class People extends TreeMap<String, Person> {
    private final LinkedHashMap<String, Person> people = new LinkedHashMap<>();

    public void analyzeAuthors(Commits commitsAll){
        Set<String> authors = commitsAll.values().stream().map(item->item.author).collect(Collectors.toSet());
        for(String author: authors){
            people.put(author, new Person(author));
        }
    }
    public int getIdOfAuthor(String author) {
        List<String> namesAuthor = new ArrayList<>(people.keySet());
        return namesAuthor.indexOf(author);
    }

    @Override public int size() {
        return people.size();
    }
    @Override public boolean isEmpty() {
        return people.isEmpty();
    }
    @Override public boolean containsKey(Object key) {
        return people.containsKey(key);
    }
    @Override public boolean containsValue(Object value) {
        return people.containsValue(value);
    }
    @Override public Person get(Object key) {
        return people.get(key);
    }
    @Override public Person put(String key, Person value) {
        return people.put(key, value);
    }
    @Override public Person remove(Object key) {
        return people.remove(key);
    }
    @Override public void putAll(Map<? extends String, ? extends Person> m) {
        people.putAll(m);
    }
    @Override public void clear() {
        people.clear();
    }
    @Override public Set<String> keySet() {
        return people.keySet();
    }
    @Override public Collection<Person> values() {
        return people.values();
    }
    @Override public Set<Map.Entry<String, Person>> entrySet() {
        return people.entrySet();
    }
}
