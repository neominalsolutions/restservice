package com.tcell_spring.restservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;


    @Column(nullable=false, length=500)
    private String content;

    @Column(name = "post_id", nullable=false)
    private Integer postId;


    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="post_id", insertable=false, updatable=false)
    // Comment nesnesi üzerinden Post create ve update işlemlerinin yapılmasını engeller.
    private Post post; // association to Post entity

    // CasadeType.ALL: Yani, Comment üzerinde yapılan işlemler (örneğin, silme) Post nesnesine de yansır.
    // CascadeType.PERSIST: Yani, Comment nesnesi kaydedildiğinde (persist edildiğinde) ilişkili Post nesnesi de otomatik olarak kaydedilir.
    // CascadeType.MERGE: Yani, Comment nesnesi güncellendiğinde (merge edildiğinde) ilişkili Post nesnesi de otomatik olarak güncellenir.
    // CascadeType.REMOVE: Yani, Comment nesnesi silindiğinde (remove edildiğinde) ilişkili Post nesnesi de otomatik olarak silinir.

    // FetchType.LAZY: İlişkili Post nesnesi, Comment nesnesi üzerinden erişildiğinde yüklenir. Yani, Comment nesnesi yüklendiğinde Post nesnesi hemen yüklenmez, sadece ihtiyaç duyulduğunda (yani, post alanına erişildiğinde) yüklenir.
    // Bu, performansı artırabilir çünkü gereksiz yere ilişkili nesnelerin yüklenmesini önler.
    // Eager fetching (FetchType.EAGER) ise, Comment nesnesi yüklendiğinde ilişkili Post nesnesinin de hemen yükleneceği anlamına gelir.

}
