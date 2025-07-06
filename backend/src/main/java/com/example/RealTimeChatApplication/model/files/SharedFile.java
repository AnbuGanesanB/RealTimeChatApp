package com.example.RealTimeChatApplication.model.files;

import com.example.RealTimeChatApplication.model.message.Message;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shared_file")
public class SharedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String originalFileName;
    private String uniqueFileName;
    private String filePath;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "linked_message_id")
    private Message linkedMessage;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SharedFile sharedFile = (SharedFile) o;
        return Objects.equals(id, sharedFile.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", originalFileName='" + originalFileName + '\'' +
                ", uniqueFileName='" + uniqueFileName + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }

}
