package nl.kolkos.cryptoManager;


import org.springframework.stereotype.Repository;

import nl.kolkos.cryptoManager.Coin;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CoinRepository extends JpaRepository<Coin, Long> {

}

