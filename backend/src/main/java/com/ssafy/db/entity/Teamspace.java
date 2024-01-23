package com.ssafy.db.entity;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Teamspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    Long teamspaceIdx;

    @NotNull
    String teamName;
    @NotNull
    LocalDateTime startDate;
    @NotNull
    LocalDateTime endDate;
    @NotNull
    String host;

    @ManyToOne
    @JoinColumn(name="TEAMSPACE_PICTURE_FILE_IDX", referencedColumnName="fileIdx")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    File teamspace_picture_file_idx;
}
