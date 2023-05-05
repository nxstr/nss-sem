package cz.cvut.fel.nss.chatgc.repository;

import cz.cvut.fel.nss.chatgc.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Integer> {
}
