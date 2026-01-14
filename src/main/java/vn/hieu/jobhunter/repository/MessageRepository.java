package vn.hieu.jobhunter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.hieu.jobhunter.domain.Message;
import vn.hieu.jobhunter.domain.User;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderAndReceiverOrderByCreatedAtAsc(User sender, User receiver);

    List<Message> findByReceiverAndSenderOrderByCreatedAtAsc(User receiver, User sender);

}
