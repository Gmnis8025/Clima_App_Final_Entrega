package com.example.clima_app_final.ciudad


import com.example.clima_app_final.ciudad.data.CiudadesRepositoryMock
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CiudadesRepositoryTest {

    private val repo = CiudadesRepositoryMock()

    @Test
    fun `cuando busco Buenos Aires obtengo al menos un resultado`() = runBlocking {
        // Act
        val resultado = repo.buscarCiudadesPorNombre("Buenos Aires")

        // Assert
        assertTrue(resultado.isNotEmpty())
        assertEquals("Buenos Aires", resultado.first().nombre)
    }

    @Test
    fun `cuando busco una ciudad inexistente la lista vuelve vacia`() = runBlocking {
        // Act
        val resultado = repo.buscarCiudadesPorNombre("CiudadInventada123")

        // Assert
        assertTrue(resultado.isEmpty())
    }


    @Test
    fun `buscar por geolocalizacion devuelve una ciudad valida`() = runBlocking {

        //Act
        val ciudad = repo.obtenerCiudadPorGeolocalizacion(-34.6037, -58.3816)


        // Assert
        assertNotNull(ciudad)
        assertEquals("Buenos Aires", ciudad?.nombre)
    }


}

