package data;

import java.util.*;
import java.util.stream.Collectors;

public class Committers extends TreeMap<String, Committer> {
    private final LinkedHashMap<String, Committer> committers = new LinkedHashMap<>();

    public void analyzeAuthors(Commits commitsAll){
        Set<String> authors = commitsAll.values().stream().map(item->item.author).collect(Collectors.toSet());
        for(String author: authors){
            committers.put(author, new Committer(author));
        }
    }
    public int getIdOfAuthor(String author) {
        List<String> namesAuthor = new ArrayList<>(committers.keySet());
        return namesAuthor.indexOf(author);
    }

    @Override public int size() {
        return committers.size();
    }
    @Override public boolean isEmpty() {
        return committers.isEmpty();
    }
    @Override public boolean containsKey(Object key) {
        return committers.containsKey(key);
    }
    @Override public boolean containsValue(Object value) {
        return committers.containsValue(value);
    }
    @Override public Committer get(Object key) {
        return committers.get(key);
    }
    @Override public Committer put(String key, Committer value) {
        return committers.put(key, value);
    }
    @Override public Committer remove(Object key) {
        return committers.remove(key);
    }
    @Override public void putAll(Map<? extends String, ? extends Committer> m) {
        committers.putAll(m);
    }
    @Override public void clear() {
        committers.clear();
    }
    @Override public Set<String> keySet() {
        return committers.keySet();
    }
    @Override public Collection<Committer> values() {
        return committers.values();
    }
    @Override public Set<Map.Entry<String, Committer>> entrySet() {
        return committers.entrySet();
    }
}
