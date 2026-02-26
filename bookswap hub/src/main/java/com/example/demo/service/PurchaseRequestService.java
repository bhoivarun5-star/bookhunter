package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.PurchaseRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseRequestService {

    private final PurchaseRequestRepository requestRepository;

    public PurchaseRequestService(PurchaseRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    // ─── Send a buy request ────────────────────────────────────────────────────
    public PurchaseRequest sendRequest(Book book, User requester, String message) {
        if (book.getOwner().getId().equals(requester.getId())) {
            throw new IllegalArgumentException("You cannot request to buy your own book.");
        }
        if (book.isSold()) {
            throw new IllegalArgumentException("This book is already sold.");
        }
        if (requestRepository.existsByBookAndRequester(book, requester)) {
            throw new IllegalArgumentException("You have already sent a request for this book.");
        }
        PurchaseRequest req = new PurchaseRequest();
        req.setBook(book);
        req.setRequester(requester);
        req.setMessage(message);
        req.setStatus(RequestStatus.PENDING);
        return requestRepository.save(req);
    }

    // ─── Get requests sent by a user ──────────────────────────────────────────
    public List<PurchaseRequest> getRequestsByUser(User user) {
        return requestRepository.findByRequesterOrderByCreatedAtDesc(user);
    }

    // ─── Get requests received by a book owner ────────────────────────────────
    public List<PurchaseRequest> getRequestsForOwner(User owner) {
        return requestRepository.findByBookOwnerOrderByCreatedAtDesc(owner);
    }

    // ─── Approve a request ────────────────────────────────────────────────────
    public void approveRequest(Long requestId, User owner) {
        PurchaseRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (!req.getBook().getOwner().getId().equals(owner.getId())) {
            throw new SecurityException("Not authorized");
        }
        req.setStatus(RequestStatus.APPROVED);
        requestRepository.save(req);
    }

    // ─── Reject a request ─────────────────────────────────────────────────────
    public void rejectRequest(Long requestId, User owner) {
        PurchaseRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (!req.getBook().getOwner().getId().equals(owner.getId())) {
            throw new SecurityException("Not authorized");
        }
        req.setStatus(RequestStatus.REJECTED);
        requestRepository.save(req);
    }
}
