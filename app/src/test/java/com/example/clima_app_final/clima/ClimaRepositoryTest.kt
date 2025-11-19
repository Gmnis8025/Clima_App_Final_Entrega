package com.example.clima_app_final.clima

import com.example.clima_app_final.clima.data.ClimaRepositoryMock
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test


class ClimaRepositoryTest {

    private val repo = ClimaRepositoryMock()

    @Test
    fun `cuando pido el clima actual recibo un valor no nulo`() = runBlocking {
        val clima = repo.obtenerClimaHoy(0.0, 0.0)

        assertNotNull(clima)
        assertTrue(clima!!.temperaturaActual > -50)
    }

    @Test
    fun `cuando pido el pronostico recibo una lista con varios dias`() = runBlocking {
        val lista = repo.obtenerPronostico5Dias(0.0, 0.0)

        //  devuelve 5 días
        assertEquals(5, lista.size)
    }

    @Test
    fun `el pronostico tiene datos coherentes`() = runBlocking {
        val lista = repo.obtenerPronostico5Dias(0.0, 0.0)

        val dia = lista.first()

        assertTrue(dia.dia.isNotBlank())

        assertTrue(dia.temperaturaMin <= dia.temperaturaMax)
    }

}
