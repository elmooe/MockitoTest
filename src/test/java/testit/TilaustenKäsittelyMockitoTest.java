package testit;

import main.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TilaustenKäsittelyMockitoTest {
    @Mock
    IHinnoittelija hinnoittelijaMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testaaAlle100() {
        float alkuSaldo = 150.0f;
        float listaHinta = 90.0f;
        float alennus = 10.0f;
        float loppuSaldo = alkuSaldo - (listaHinta * (1 - alennus / 100));

        Asiakas asiakas = new Asiakas(alkuSaldo);
        Tuote tuote = new Tuote("TDD in Action", listaHinta);

        when(hinnoittelijaMock.getAlennusProsentti(asiakas, tuote)).thenReturn(alennus, alennus);
        doNothing().when(hinnoittelijaMock).aloita();
        doNothing().when(hinnoittelijaMock).lopeta();

        TilaustenKäsittely käsittelijä = new TilaustenKäsittely();
        käsittelijä.setHinnoittelija(hinnoittelijaMock);

        käsittelijä.käsittele(new Tilaus(asiakas, tuote));

        assertEquals(loppuSaldo, asiakas.getSaldo(), 0.001);
        verify(hinnoittelijaMock, times(2)).getAlennusProsentti(asiakas, tuote);
        verify(hinnoittelijaMock, never()).setAlennusProsentti(any(Asiakas.class), anyFloat());
        verify(hinnoittelijaMock).aloita();
        verify(hinnoittelijaMock).lopeta();
    }

    @Test
    public void testaaYli100() {
        float alkuSaldo = 200.0f;
        float listaHinta = 120.0f;
        float alennus = 15.0f;
        float loppuSaldo = alkuSaldo - (listaHinta * (1 - alennus / 100));

        Asiakas asiakas = new Asiakas(alkuSaldo);
        Tuote tuote = new Tuote("TDD in Action", listaHinta);

        when(hinnoittelijaMock.getAlennusProsentti(asiakas, tuote)).thenReturn(10.0f, 15.0f);
        doNothing().when(hinnoittelijaMock).aloita();
        doNothing().when(hinnoittelijaMock).lopeta();

        TilaustenKäsittely käsittelijä = new TilaustenKäsittely();
        käsittelijä.setHinnoittelija(hinnoittelijaMock);

        käsittelijä.käsittele(new Tilaus(asiakas, tuote));

        assertEquals(loppuSaldo, asiakas.getSaldo(), 0.001);
        verify(hinnoittelijaMock, times(2)).getAlennusProsentti(asiakas, tuote);
        verify(hinnoittelijaMock).setAlennusProsentti(asiakas, 15.0f);
        verify(hinnoittelijaMock).aloita();
        verify(hinnoittelijaMock).lopeta();
    }
}
