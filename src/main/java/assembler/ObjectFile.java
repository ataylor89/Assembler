package assembler;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
public class ObjectFile {

    private ByteArray file;

    private class Section {
        public String name;
        public int start, end;

        public Section(String name, int start, int end) {
            this.name = name;
            this.start = start;
            this.end = end;
        }
    }

    private List<Section> sections; 

    public ObjectFile() {
        file = new ByteArray();
        sections = new ArrayList<Section>();
    }

    public void setFile(ByteArray file) {
        this.file = file;
    }
    
    public ByteArray getFile() {
        return file;
    }
    
    public void setSections(List<Section> sections) {
        this.sections = sections;
    }
    
    public List<Section> getSections() {
        return sections;
    }
    
    public void addSection(byte[] bytes, String name) {
        int start = file.getIndex();
        int end = file.getIndex() + bytes.length;
        file.addBytes(bytes);      
        Section section = new Section(name, start, end);
        sections.add(section);
    }
    
    public void addSection(byte[] bytes, String name, boolean padZeroes) {
        int start = file.getIndex();
        int end = file.getIndex() + bytes.length;
        file.addBytes(bytes);
        
        if (padZeroes) {
            int n = 8 - (bytes.length % 8);
            for (int i = 0; i < n; i++) {
                file.addByte((byte) 0);
                end++;
            }
        }
        
        Section section = new Section(name, start, end);
        sections.add(section);
    }

    public byte[] getSection(int index) {
        Section section = sections.get(index);
        return file.getBytes(section.start, section.end);
    }

    public byte[] getSection(String name) {
        for (int i = 0; i < sections.size(); i++) 
            if (sections.get(i).name.equals(name))
                return getSection(i);
        return null;
    }

    public String getSectionName(int index) {
        return sections.get(index).name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Object file (%d bytes)\n", file.getIndex()));
        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            byte[] bytes = getSection(i);
            sb.append(section.name);
            sb.append("\n");
            sb.append(Bytes.hexstring(bytes));  
            sb.append("\n");
        }
        return sb.toString();
    }
}
