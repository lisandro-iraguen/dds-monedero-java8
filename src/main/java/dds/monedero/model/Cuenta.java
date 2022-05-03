package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo;
  private List<Deposito> depositos = new ArrayList<>();
  private List<Extraccion> extracciones = new ArrayList<>();

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }



  public void poner(double cuanto) {
    varificarSaldoPositivo(cuanto);

    if (getDepositos().stream().count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
    agregarDeposito(LocalDate.now(),cuanto);
  }
  public List<Extraccion> getExtracciones(){
    return extracciones;
  }
  public List<Deposito> getDepositos(){
    return depositos;
  }

  public void sacar(double cuanto) {
    varificarSaldoPositivo(cuanto);

    if (getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy;
    if (cuanto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }

    agregarExtraccion(LocalDate.now(),cuanto);
  }

  public void agregarExtraccion(LocalDate fecha, double cuanto) {
    Extraccion movimiento = new Extraccion(fecha, cuanto);
    extracciones.add(movimiento);
  }
  public void agregarDeposito(LocalDate fecha, double cuanto) {
    Deposito movimiento = new Deposito(fecha, cuanto);
    depositos.add(movimiento);
  }
  public double getMontoExtraidoA(LocalDate fecha) {
    return getExtracciones().stream()
        .filter(movimiento -> movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }



  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

  private void varificarSaldoPositivo(double cuanto) {
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
  }
}
