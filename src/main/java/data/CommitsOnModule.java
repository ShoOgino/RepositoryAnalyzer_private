package data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import misc.DeserializerModification;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;

import java.util.*;
import java.util.stream.Collectors;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CommitsOnModule implements Map<MultiKey<? extends String>, CommitOnModule> {
    @JsonDeserialize(keyUsing = DeserializerModification.class) private final MultiKeyMap<String, CommitOnModule> modifications = new MultiKeyMap<>();
    @Override public int size() { return modifications.size(); }
    @Override public boolean isEmpty() { return modifications.isEmpty(); }
    @Override public boolean containsKey(Object key) { return modifications.containsKey(key); }
    @Override public boolean containsValue(Object value) { return modifications.containsValue(value); }
    @Override public CommitOnModule get(Object key) { return modifications.get(key); }
    public CommitOnModule get(String idCommitParent, String idCommit, String pathOld, String pathNew){
        return modifications.get(idCommitParent, idCommit, pathOld, pathNew);
    }
    public List<CommitOnModule> findFromIdCommit(String idCommit) {
        return modifications.values().stream().filter(a->a.idCommit.equals(idCommit)).collect(Collectors.toList());
    }
    public List<CommitOnModule> findFromPathOld(String pathOld) {
        return modifications.values().stream().filter(a->a.pathOld.equals(pathOld)).collect(Collectors.toList());
    }
    public List<CommitOnModule> findFromPathNew(String pathNew) {
        return modifications.values().stream().filter(a->a.pathNew.equals(pathNew)).collect(Collectors.toList());
    }
    public void put(String idCommitParent, String idCommit, String pathOld, String pathNew, CommitOnModule commitOnModule){
        modifications.put(idCommitParent, idCommit, pathOld, pathNew, commitOnModule);
    }
    @Override public CommitOnModule put(MultiKey<? extends String> key, CommitOnModule value) { return modifications.put(key, value); }
    @Override public CommitOnModule remove(Object key) { return modifications.remove(key); }
    @Override public void putAll(Map<? extends MultiKey<? extends String>, ? extends CommitOnModule> m) { modifications.putAll(m); }
    @Override public void clear(){ modifications.clear(); }
    @Override public Set<MultiKey<? extends String>> keySet() { return modifications.keySet(); }
    @Override public Collection<CommitOnModule> values(){ return modifications.values(); }
    @Override public Set<Entry<MultiKey<? extends String>, CommitOnModule>> entrySet() { return modifications.entrySet(); }
}
