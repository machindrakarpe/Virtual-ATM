package domain;

import java.math.BigDecimal;

public record TransferRequest(String sender, String beneficiary, BigDecimal amount){};
