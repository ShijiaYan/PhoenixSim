package edu.columbia.lrl.CrossLayer.physical_models.util;

import ch.epfl.general_libraries.clazzes.ParamName;

public class ChannelsAndBandwidthFormat extends AbstractLinkFormat {
	
	public double totalRate;
	public int channels;
	
	public ChannelsAndBandwidthFormat(
			@ParamName(name="Number of channels") int channels, 
			@ParamName(name="Total rate in Gb/s") double totalRate) {
		this.totalRate = totalRate;
		this.channels = channels;
	}

	@Override
	public double getWavelengthRate() {
		return totalRate*1e9/(double)channels;
	}

	@Override
	public int getNumberOfChannels() {
		return channels;
	}

	@Override
	public boolean isNumberOfChannelFixed() {
		return (channels > 0);
	}

}
