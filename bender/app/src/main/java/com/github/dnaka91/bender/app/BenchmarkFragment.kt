package com.github.dnaka91.bender.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.dnaka91.bender.app.databinding.FragmentBenchmarkBinding
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@ExperimentalTime
class BenchmarkFragment : Fragment() {
    private var _binding: FragmentBenchmarkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBenchmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.timing.text = getString(R.string.default_timing)

        binding.empty.setOnClickListener {
            runBenchmark {
                RUNNER.empty()
            }
        }

        binding.hello.setOnClickListener {
            runBenchmark {
                RUNNER.hello("Bender")
            }
        }

        binding.mandelbrot.setOnClickListener {
            runBenchmark {
                RUNNER.mandelbrot(100, 100)
            }
        }

        binding.mandelbrotManual.setOnClickListener {
            runBenchmark {
                RUNNER.mandelbrotManual(100, 100)
            }
        }

        binding.loadPerson.setOnClickListener {
            runBenchmark {
                RUNNER.loadPerson(5)
            }
        }

        binding.savePerson.setOnClickListener {
            runBenchmark {
                RUNNER.savePerson()
            }
        }

        binding.loadPersonJson.setOnClickListener {
            runBenchmark {
                RUNNER.loadPersonJson(5)
            }
        }

        binding.savePersonJson.setOnClickListener {
            runBenchmark {
                RUNNER.savePersonJson()
            }
        }

        binding.callback.setOnClickListener {
            runBenchmark {
                RUNNER.callback()
            }
        }

        enableControls(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun runBenchmark(f: () -> Unit) {
        val stop = AtomicBoolean(false)
        enableControls(false)

        val threadCount = Runtime.getRuntime().availableProcessors()
        val mutex = ReentrantLock()

        var runningThreads = threadCount
        var totalIterations = 0
        var totalDuration = Duration.ZERO

        binding.stop.setOnClickListener {
            stop.set(true)
        }


        (0..threadCount).forEach { _ ->
            thread {
                val res = measureTimedValue {
                    var iterations = 0
                    while (!stop.get()) {
                        f()
                        iterations++
                    }
                    iterations
                }

                val done = mutex.withLock {
                    totalIterations += res.value
                    totalDuration += res.duration
                    runningThreads--

                    runningThreads == 0
                }

                if (done) {
                    requireActivity().runOnUiThread {
                        setTiming(totalDuration / totalIterations)
                        enableControls(true)
                    }
                }
            }
        }
    }

    private fun enableControls(enable: Boolean) {
        fun View.updateEnabled(enable: Boolean, feature: Feature) {
            isEnabled = enable && RUNNER.features.contains(feature)
        }

        binding.empty.updateEnabled(enable, Feature.Empty)
        binding.hello.updateEnabled(enable, Feature.Hello)
        binding.mandelbrot.updateEnabled(enable, Feature.Mandelbrot)
        binding.mandelbrotManual.updateEnabled(enable, Feature.MandelbrotManual)
        binding.loadPerson.updateEnabled(enable, Feature.LoadPerson)
        binding.savePerson.updateEnabled(enable, Feature.SavePerson)
        binding.loadPersonJson.updateEnabled(enable, Feature.LoadPersonJson)
        binding.savePersonJson.updateEnabled(enable, Feature.SavePersonJson)
        binding.callback.updateEnabled(enable, Feature.Callback)
        binding.stop.isEnabled = !enable
    }

    private fun setTiming(duration: Duration) {
        binding.timing.text = duration.toString()
    }

    companion object {
        fun newInstance() = BenchmarkFragment()
    }
}