package com.example.multimediaapi.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Member {

    private boolean isOwner;

    private boolean isEditor;

    @ManyToOne
    private User user;

    public Member(User user) {
        this.user = user;
    }

    public boolean isOwnerOrEditor() {
        return this.isOwner || this.isEditor;
    }

    public boolean isOwner() {
        return this.isOwner;
    }
}
