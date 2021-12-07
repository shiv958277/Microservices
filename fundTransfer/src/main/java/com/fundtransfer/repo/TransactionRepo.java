package com.fundtransfer.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fundtransfer.entity.Transactions;

@Repository
public interface TransactionRepo extends JpaRepository<Transactions, Integer> {

}